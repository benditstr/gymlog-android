import { useState } from "react"
import type { Run, RunCategory } from "@/types"

type AddRunFormProps = {
  onAdd: (run: Omit<Run, "id" | "avgPaceMinPerKm">) => void
}

export function AddRunForm({ onAdd }: AddRunFormProps) {
  // 1. States hier rein
  const [date, setDate] = useState("")
  const [distanceKm, setDistanceKm] = useState("")
  const [durationMin, setDurationMin] = useState("")
  const [category, setCategory] = useState<RunCategory>("Kurzlauf")

  // 2. Funktionen hier rein
  function handleSubmit(e: React.FormEvent) {
    e.preventDefault()
    if (date.length === 0 || distanceKm.length === 0) return
    onAdd({ date, distanceKm: Number(distanceKm), durationMin: Number(durationMin), category })
    setDate("")
    setDistanceKm("")
    setDurationMin("")
  }

  // 3. JSX (return) hier rein
  return (
    <form onSubmit={handleSubmit} className="p-4 space-y-3">
      <input
        value={date}
        onChange={e => setDate(e.target.value)}
        placeholder="Datum (YYYY-MM-DD)"
        className="border rounded px-3 py-2 w-full"
      />
      <input
        value={distanceKm}
        onChange={e => setDistanceKm(e.target.value)}
        placeholder="Distanz in km (z.B. 10.5)"
        className="border rounded px-3 py-2 w-full"
        />
        
        <input
        value={durationMin}
        onChange={e => setDurationMin(e.target.value)}
        placeholder="Dauer in Minuten (z.B. 55)"
        className="border rounded px-3 py-2 w-full"
        />

      <button type="submit" className="bg-blue-600 text-white px-4 py-2 rounded">
        Hinzufügen
      </button>
    </form>
  )
}

