import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import type { Run, RunCategory } from '@/types'
import { Header } from '@/components/Header'
import { FilterBar } from '@/components/FilterBar'
import { ItemList } from '@/components/ItemList'
import { ItemDetail } from '@/components/ItemDetail'
import { AddRunForm } from '@/components/AddRunForm'
import { ItemListSkeleton } from '@/components/ItemCardSkeleton'
import { fetchRuns, createRun, updateRun, deleteRun } from '@/api'

function App() {
  const queryClient = useQueryClient()

  const { data: runs = [], isLoading, error } = useQuery<Run[]>({
    queryKey: ['runs'],
    queryFn: fetchRuns,
  })

  const [showForm, setShowForm] = useState(false)
  const [activeCategory, setActiveCategory] = useState<RunCategory | 'Alle'>('Alle')
  const [selectedRun, setSelectedRun] = useState<Run | null>(null)
  const [editId, setEditId] = useState<number | null>(null)

  const filteredRuns = activeCategory === 'Alle'
    ? runs
    : runs.filter(run => run.category === activeCategory)

  const createMutation = useMutation({
    mutationFn: createRun,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['runs'] })
      setShowForm(false)
    },
  })

  const deleteMutation = useMutation({
    mutationFn: deleteRun,
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['runs'] }),
  })

  const updateMutation = useMutation({
    mutationFn: ({ id, updated }: { id: number, updated: Partial<Run> }) => {
      const existing = runs.find(r => r.id === id)!
      const merged = { ...existing, ...updated }
      const avgPaceMinPerKm = merged.durationMin / merged.distanceKm
      return updateRun(id, { ...merged, avgPaceMinPerKm })
    },
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['runs'] }),
  })

  if (error) return <p className="p-4 text-red-500">Fehler: {error.message}</p>

  return (
    <div className="min-h-screen bg-gray-50">
      <Header />
      <button onClick={() => setShowForm(!showForm)}>
        {showForm ? "Formular schließen" : "Lauf hinzufügen"}
      </button>
      <FilterBar
        activeCategory={activeCategory}
        onCategoryChange={setActiveCategory}
      />
      <ItemDetail
        run={selectedRun}
        onClose={() => setSelectedRun(null)}
      />
      {isLoading ? (
        <ItemListSkeleton />
      ) : (
        <ItemList
          runs={filteredRuns}
          onSelect={setSelectedRun}
          onDelete={(id) => deleteMutation.mutate(id)}
          editingId={editId}
          onEditToggle={setEditId}
          onEdit={(id, updated) => updateMutation.mutate({ id, updated })}
        />
      )}
      {showForm && <AddRunForm onAdd={(run) => createMutation.mutate(run)} />}
    </div>
  )
}

export default App
