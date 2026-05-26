import type { Run } from '@/types'

export async function fetchRuns(): Promise<Run[]> {
  const response = await fetch('/api/runs')
  if (!response.ok) throw new Error('Fehler beim Laden der Läufe')
  return response.json() as Promise<Run[]>
}

export async function createRun(run: Omit<Run, 'id' | 'avgPaceMinPerKm'>): Promise<Run> {
  const avgPaceMinPerKm = run.durationMin / run.distanceKm
  const response = await fetch('/api/runs', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ ...run, avgPaceMinPerKm }),
  })
  if (!response.ok) throw new Error('Fehler beim Erstellen')
  return response.json() as Promise<Run>
}

export async function updateRun(id: number, run: Omit<Run, 'id'>): Promise<Run> {
  const response = await fetch(`/api/runs/${id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(run),
  })
  if (!response.ok) throw new Error('Fehler beim Aktualisieren')
  return response.json() as Promise<Run>
}

export async function deleteRun(id: number): Promise<void> {
  const response = await fetch(`/api/runs/${id}`, { method: 'DELETE' })
  if (!response.ok) throw new Error('Fehler beim Löschen')
}
