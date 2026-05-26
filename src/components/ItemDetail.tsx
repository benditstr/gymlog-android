import type { Run } from '@/types'

type ItemDetailProps = {
  run: Run | null
  onClose: () => void
}

export function ItemDetail({ run, onClose }: ItemDetailProps) {
  if (run === null) {
    return null
  }

  return (
    <div className="border rounded p-6 m-4 bg-white shadow-lg">
      <button
        onClick={onClose}
        className="text-sm text-gray-500 hover:text-gray-800 mb-4"
      >
        ← Zurück
      </button>
      <h2 className="text-xl font-bold mb-2">{run.date}</h2>
      <p>Distanz: <strong>{run.distanceKm} km</strong></p>
      <p>Dauer: <strong>{run.durationMin} min</strong></p>
      <p>Pace: <strong>{run.avgPaceMinPerKm} min/km</strong></p>
      <p>Kategorie: <strong>{run.category}</strong></p>
      {run.notes && <p>{run.notes}</p>}

    </div>
    
  )
}
