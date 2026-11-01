import { useEffect, useState } from 'react'
import { getApiStatus, type ApiStatus } from './services/api'

function App() {
  const [apiStatus, setApiStatus] = useState<ApiStatus | null>(null)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    getApiStatus()
      .then(setApiStatus)
      .catch(() => setError('API indisponível. Inicie o backend para conectar.'))
  }, [])

  return (
    <main className="page">
      <header className="topbar">
        <div className="brand-mark">O</div>
        <div>
          <strong>OdontoConnect</strong>
          <span>Central de Atendimento</span>
        </div>
        <span className="demo-tag">AMBIENTE DEMO</span>
      </header>

      <section className="hero">
        <span className="eyebrow">PLATAFORMA DE ATENDIMENTO OMNICHANNEL</span>
        <h1>Um só lugar para acolher, orientar e ouvir.</h1>
        <p>
          Fundação do ambiente de desenvolvimento concluída. Nas próximas etapas,
          vamos construir os módulos de usuários, escalas, atendimentos e ouvidoria.
        </p>

        <div className="status-card">
          <div>
            <strong>Conexão com o backend</strong>
            <span>
              {apiStatus
                ? `${apiStatus.application} · ${apiStatus.environment}`
                : error ?? 'Verificando conexão...'}
            </span>
          </div>
          <span className={`status-indicator ${apiStatus ? 'online' : 'offline'}`}>
            {apiStatus ? 'ONLINE' : 'OFFLINE'}
          </span>
        </div>
      </section>

      <section className="modules">
        <article><span>01</span><h2>Atendimento</h2><p>Conversas e solicitações centralizadas.</p></article>
        <article><span>02</span><h2>Escalas</h2><p>Organização dos turnos de alunos e professores.</p></article>
        <article><span>03</span><h2>Ouvidoria</h2><p>Manifestações com protocolo e histórico.</p></article>
      </section>

      <footer>OdontoConnect · Protótipo de desenvolvimento · Dados fictícios</footer>
    </main>
  )
}

export default App
