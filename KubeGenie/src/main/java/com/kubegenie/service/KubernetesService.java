package com.kubegenie.service;

import com.kubegenie.model.*;
import io.kubernetes.client.custom.V1Patch;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.apis.EventsV1Api;
import io.kubernetes.client.openapi.models.*;
import io.kubernetes.client.util.generic.options.ListOptions;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class KubernetesService {

    private final ApiClient client;

    public KubernetesService(ApiClient client) {
        this.client = client;
    }

    public ClusterState snapshotClusterState() {
        CoreV1Api core = new CoreV1Api(client);
        AppsV1Api apps = new AppsV1Api(client);
        EventsV1Api events = new EventsV1Api(client);

        ClusterState state = new ClusterState();
        state.setCapturedAt(OffsetDateTime.now());

        // Pods
        List<PodInfo> pods = new ArrayList<>();
        try {
            V1PodList podList = core.listPodForAllNamespaces()
                    .timeoutSeconds(20)
                    .execute();
            for (V1Pod p : podList.getItems()) {
                PodInfo info = new PodInfo();
                info.setName(p.getMetadata().getName());
                info.setNamespace(p.getMetadata().getNamespace());
                if (p.getStatus() != null) {
                    info.setPhase(p.getStatus().getPhase());
                    info.setNodeName(p.getSpec() != null ? p.getSpec().getNodeName() : null);
                    info.setStartTime(p.getStatus().getStartTime());
                    info.setQosClass(p.getStatus().getQosClass());
                    if (p.getStatus().getConditions() != null) {
                        info.setConditions(p.getStatus().getConditions().stream()
                                .map(c -> c.getType() + ":" + c.getStatus())
                                .collect(Collectors.toList()));
                    }
                }
                if (p.getSpec() != null && p.getSpec().getContainers() != null) {
                    info.setContainerImages(p.getSpec().getContainers().stream()
                            .map(V1Container::getImage).collect(Collectors.toList()));
                }
                info.setLabels(p.getMetadata().getLabels());
                pods.add(info);
            }
        } catch (Exception e) {
            // If listing pods fails, keep pods empty but proceed
        }
        state.setPods(pods);

        // Nodes
        List<NodeInfo> nodes = new ArrayList<>();
        try {
            V1NodeList nodeList = core.listNode().timeoutSeconds(20).execute();
            for (V1Node n : nodeList.getItems()) {
                NodeInfo ni = new NodeInfo();
                ni.setName(n.getMetadata().getName());
                if (n.getStatus() != null) {
                    if (n.getStatus().getNodeInfo() != null) {
                        ni.setOsImage(n.getStatus().getNodeInfo().getOsImage());
                        ni.setKubeletVersion(n.getStatus().getNodeInfo().getKubeletVersion());
                    }
                    if (n.getStatus().getConditions() != null) {
                        ni.setConditions(n.getStatus().getConditions().stream()
                                .map(c -> c.getType() + ":" + c.getStatus())
                                .collect(Collectors.toList()));
                    }
                    Map<String, Quantity> capacity = n.getStatus().getCapacity();
                    Map<String, Quantity> alloc = n.getStatus().getAllocatable();
                    if (capacity != null) {
                        ni.setCapacityCpu(Optional.ofNullable(capacity.get("cpu")).map(Quantity::toSuffixedString).orElse(null));
                        ni.setCapacityMem(Optional.ofNullable(capacity.get("memory")).map(Quantity::toSuffixedString).orElse(null));
                    }
                    if (alloc != null) {
                        ni.setAllocatableCpu(Optional.ofNullable(alloc.get("cpu")).map(Quantity::toSuffixedString).orElse(null));
                        ni.setAllocatableMem(Optional.ofNullable(alloc.get("memory")).map(Quantity::toSuffixedString).orElse(null));
                    }
                }
                if (n.getSpec() != null && n.getSpec().getTaints() != null) {
                    ni.setTaints(n.getSpec().getTaints().stream()
                            .map(t -> t.getKey() + "=" + t.getValue() + ":" + t.getEffect())
                            .collect(Collectors.joining(",")));
                }
                ni.setLabels(n.getMetadata().getLabels());
                nodes.add(ni);
            }
        } catch (Exception e) {
            // swallow for skeleton
        }
        state.setNodes(nodes);

        // Deployments
        List<DeploymentInfo> deps = new ArrayList<>();
        try {
            V1DeploymentList depList = apps.listDeploymentForAllNamespaces().timeoutSeconds(20).execute();
            for (V1Deployment d : depList.getItems()) {
                DeploymentInfo di = new DeploymentInfo();
                di.setName(d.getMetadata().getName());
                di.setNamespace(d.getMetadata().getNamespace());
                if (d.getSpec() != null) di.setReplicas(d.getSpec().getReplicas());
                if (d.getStatus() != null) {
                    di.setAvailableReplicas(d.getStatus().getAvailableReplicas());
                    di.setUpdatedReplicas(d.getStatus().getUpdatedReplicas());
                }
                deps.add(di);
            }
        } catch (Exception e) {
            // swallow
        }
        state.setDeployments(deps);

        // Events (last 50)
        List<EventInfo> evs = new ArrayList<>();
        try {
            var evList = events.listEventForAllNamespaces()
                    .limit(50)
                    .timeoutSeconds(15)
                    .execute();
            for (var ev : evList.getItems()) {
                EventInfo ei = new EventInfo();
                ei.setReason(ev.getReason());
                ei.setType(ev.getType());
                ei.setMessage(ev.getNote());
                if (ev.getRegarding() != null) {
                    String obj = ev.getRegarding().getKind() + "/" + ev.getRegarding().getName();
                    ei.setInvolvedObject(obj);
                }
                ei.setEventTime(ev.getEventTime());
                evs.add(ei);
            }
        } catch (Exception e) {
            // swallow
        }
        state.setRecentEvents(evs);

        // Metrics (optional; requires metrics-server). We'll try best-effort via /apis/metrics.k8s.io/v1beta1
        // Using generic client would add more code; here, we keep a simple placeholder map that callers can extend.
        state.setPodUsageByKey(Collections.emptyMap());
        state.setNodeUsageByName(Collections.emptyMap());

        return state;
    }

    /**
     * Convenience util: build a small digest suitable to show in the response.
     */
    public Map<String, Object> buildSummary(ClusterState state) {
        Map<String, Long> podsByPhase = state.getPods() == null ? Map.of() :
                state.getPods().stream().collect(Collectors.groupingBy(
                        p -> Optional.ofNullable(p.getPhase()).orElse("Unknown"),
                        Collectors.counting()
                ));
        return Map.of(
                "capturedAt", String.valueOf(state.getCapturedAt()),
                "podsTotal", state.getPods() == null ? 0 : state.getPods().size(),
                "podsByPhase", podsByPhase,
                "nodes", state.getNodes() == null ? 0 : state.getNodes().size(),
                "deployments", state.getDeployments() == null ? 0 : state.getDeployments().size(),
                "recentEvents", state.getRecentEvents() == null ? 0 : state.getRecentEvents().size()
        );
    }
}
