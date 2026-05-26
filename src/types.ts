export type RunCategory = 'Kurzlauf' | 'Mittellauf' | 'Langlauf' | 'Intervall' | 'Sonstiges'

export type Run= {
    id: number
    date: string
    distanceKm: number
    durationMin: number
    avgPaceMinPerKm: number
    category: RunCategory
    notes?: string
}