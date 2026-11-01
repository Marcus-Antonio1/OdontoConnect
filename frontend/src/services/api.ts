const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080'

export type ApiStatus = {
  application: string
  status: string
  environment: string
}

export async function getApiStatus(): Promise<ApiStatus> {
  const response = await fetch(`${API_BASE_URL}/api/v1/status`)

  if (!response.ok) {
    throw new Error('Não foi possível consultar a API.')
  }

  return response.json() as Promise<ApiStatus>
}
