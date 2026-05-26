import type { Run } from '@/types'
import { ItemCard } from '@/components/ItemCard'

type ItemListProps = {
  runs: Run[]
  onSelect: (run: Run) => void
  onDelete: (id: number) => void
  editingId: number | null
  onEditToggle: (id: number | null) => void
  onEdit: (id: number, updated: Partial<Run>) => void
}

export function ItemList({ runs, onSelect, onDelete, editingId, onEditToggle, onEdit }: ItemListProps) {
  if (runs.length === 0) {
    return (
      <p className="p-4 text-gray-500">Keine Läufe in dieser Kategorie.</p>
    )
  }

  return (
    <div className="grid grid-cols-1 gap-4 p-4 sm:grid-cols-2 lg:grid-cols-3">
      {runs.map(run => (
        <ItemCard
          key={run.id}
          run={run}
          onSelect={onSelect}
          onDelete={onDelete}
          isEditing={run.id === editingId}
          onEditToggle={onEditToggle}
          onEdit={onEdit}
        />
      ))}
    </div>
  )
}
