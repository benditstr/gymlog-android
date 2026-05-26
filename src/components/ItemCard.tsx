import { useState } from 'react'
import type { Run } from '@/types'

type ItemCardProps = {
  run: Run
  onSelect: (run: Run) => void
  onDelete: (id: number) => void
  isEditing: boolean
  onEditToggle: (id: number | null) => void
  onEdit: (id: number, updated: Partial<Run>) => void
}

const categoryColors = {
  Kurzlauf:   'bg-green-100 text-green-800',
  Mittellauf: 'bg-yellow-100 text-yellow-800',
  Langlauf:   'bg-red-100 text-red-800',
  Intervall:  'bg-purple-100 text-purple-800',
  Sonstiges:  'bg-gray-100 text-gray-800',
}

export function ItemCard({ run, onSelect, onDelete, isEditing, onEditToggle, onEdit }: ItemCardProps) {
  const [distanceKm, setDistanceKm] = useState(String(run.distanceKm))
  const [durationMin, setDurationMin] = useState(String(run.durationMin))

  return (
    <div
      onClick={() => onSelect(run)}
      className="border rounded p-4 cursor-pointer hover:shadow-md"
    >
      {isEditing ? (
        <>
          <input value={distanceKm} onChange={e => setDistanceKm(e.target.value)}
            className="border rounded px-2 py-1 w-full mb-1" />
          <input value={durationMin} onChange={e => setDurationMin(e.target.value)}
            className="border rounded px-2 py-1 w-full mb-1" />
          <button onClick={() => {
            onEdit(run.id, { distanceKm: Number(distanceKm), durationMin: Number(durationMin) })
            onEditToggle(null)
          }} className="text-xs bg-green-500 text-white px-2 py-1 rounded mr-2">
            Speichern
          </button>
          <button onClick={() => onEditToggle(null)} className="text-xs text-gray-500">
            Abbrechen
          </button>
        </>
      ) : (
        <>
          <p className="text-sm text-gray-500">{run.date}</p>
          <p className="text-lg font-bold">{run.distanceKm} km</p>
          <p className="text-sm">{run.avgPaceMinPerKm} min/km</p>
          <span className={`text-xs px-2 py-0.5 rounded-full ${categoryColors[run.category]}`}>
            {run.category}
          </span>
          <div className="mt-2 flex gap-2">
            <button onClick={e => { e.stopPropagation(); onEditToggle(run.id) }}
              className="text-xs text-blue-500 hover:text-blue-700">Bearbeiten</button>
            <button onClick={e => { e.stopPropagation(); onDelete(run.id) }}
              className="text-xs text-red-500 hover:text-red-700">Löschen</button>
          </div>
        </>
      )}
    </div>
  )
}
