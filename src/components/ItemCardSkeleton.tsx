export function ItemCardSkeleton() {
  return (
    <div className="border rounded p-4 animate-pulse">
      <div className="h-4 bg-gray-300 rounded w-1/3 mb-2"></div>
      <div className="h-6 bg-gray-300 rounded w-1/2 mb-2"></div>
      <div className="h-4 bg-gray-300 rounded w-1/4 mb-3"></div>
      <div className="h-5 bg-gray-300 rounded-full w-20"></div>
    </div>
  )
}

export function ItemListSkeleton() {
  return (
    <div className="grid grid-cols-1 gap-4 p-4 sm:grid-cols-2 lg:grid-cols-3">
      {[1, 2, 3, 4, 5, 6].map(i => (
        <ItemCardSkeleton key={i} />
      ))}
    </div>
  )
}
