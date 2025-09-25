import React, { useState } from 'react'

export default function App() {
  const [query, setQuery] = useState("")
  const [response, setResponse] = useState(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)

  const submit = async (e) => {
    e.preventDefault()
    setLoading(true)
    setError(null)
    setResponse(null)
    try {
      const res = await fetch("/api/query", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ query })
      })
      if (!res.ok) throw new Error(`HTTP ${res.status}`)
      const data = await res.json()
      setResponse(data)
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div style={{maxWidth: 900, margin: "2rem auto", fontFamily: "Inter, system-ui, Arial"}}>
      <h1>KubeGenie</h1>
      <p>Ask anything about your Kubernetes cluster in natural language.</p>

      <form onSubmit={submit} style={{display: "flex", gap: 8}}>
        <input
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          placeholder="e.g., Why are my pods CrashLoopBackOff in namespace foo?"
          style={{flex: 1, padding: 12, borderRadius: 8, border: "1px solid #ccc"}}
        />
        <button disabled={loading || !query.trim()} style={{padding: "12px 16px", borderRadius: 8}}>
          {loading ? "Thinking..." : "Ask"}
        </button>
      </form>

      {error && <div style={{marginTop: 16, color: "crimson"}}>Error: {error}</div>}

      {response && (
        <div style={{marginTop: 24, padding: 16, border: "1px solid #eee", borderRadius: 8}}>
          <h3>AI Explanation</h3>
          <p style={{whiteSpace: "pre-wrap"}}>{response.aiExplanation}</p>

          {response.suggestedKubectlCommands?.length > 0 && (
            <>
              <h3>Suggested kubectl Commands</h3>
              <pre style={{background:"#f8f8f8", padding:12, borderRadius:8}}>
{response.suggestedKubectlCommands.map((c,i)=>`$ ${c}`).join("\n")}
              </pre>
            </>
          )}

          {response.optimizationAdvice?.length > 0 && (
            <>
              <h3>Optimization Advice</h3>
              <ul>
                {response.optimizationAdvice.map((o,i)=><li key={i}>{o}</li>)}
              </ul>
            </>
          )}

          {response.clusterSummary && (
            <>
              <h3>Cluster Summary</h3>
              <pre style={{background:"#f8f8f8", padding:12, borderRadius:8}}>
{JSON.stringify(response.clusterSummary, null, 2)}
              </pre>
            </>
          )}
        </div>
      )}
    </div>
  )
}
