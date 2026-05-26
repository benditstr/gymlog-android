import type { RunCategory } from "@/types"

type FilterBarProps = {
    activeCategory: RunCategory | 'Alle'
    onCategoryChange: (category: RunCategory | 'Alle') => void

}

const categories: (RunCategory | 'Alle')[] = ['Alle', 'Kurzlauf', 'Mittellauf', 'Langlauf', 'Intervall', 'Sonstiges']

export function FilterBar ({ activeCategory, onCategoryChange }: FilterBarProps) {
    return (
        <div className="flex gap-2 p-4">
            {categories.map(cat => (
                <button
                    key={cat}
                    onClick={() => onCategoryChange(cat)}
                    className={activeCategory === cat 
                        ? 'bg-blue-600 text-white px-3 py-1 rounded'
                        : 'bg-gray-200 text-gray-700 px-3 py-1 rounded'
                    }
                >{cat}</button>
            ))}
        </div>
    )
}