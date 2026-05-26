import express from "express";
import { join, dirname } from "path";
import { fileURLToPath } from "url";
import { Run } from "./types.js";

const _dir = dirname(fileURLToPath(import.meta.url));

const app = express();
const PORT = 3000;

// Statische Dateien aus dem public/ Ordner ausliefern
app.use(express.static(join(_dir, "..", "public")));

// Middleware, um JSON im Request-Body zu parsen
app.use(express.json());

// --- In-Memory Daten ---
const runs: Run[] = [
  { id: 1, date: "2026-03-10", distanceKm: 10.2, durationMin: 58, avgPaceMinPerKm: 5.69, category: "Mittellauf", notes: "Fühlte mich gut" },
  { id: 2, date: "2026-03-13", distanceKm: 21.1, durationMin: 118, avgPaceMinPerKm: 5.59, category: "Langlauf" },
  { id: 3, date: "2026-03-17", distanceKm: 8.0,  durationMin: 44, avgPaceMinPerKm: 5.5,  category: "Kurzlauf" },
];

let nextId = 4;

// --- Endpunkte ---

// GET /api/runs — alle Läufe
app.get("/api/runs", (_req, res) => {
  res.json(runs);
});

// GET /api/runs/:id — einzelner Lauf
app.get("/api/runs/:id", (req, res) => {
  const run = runs.find(r => r.id === Number(req.params.id));
  if (!run) {
    res.status(404).json({ error: "Run nicht gefunden" });
    return;
  }
  res.json(run);
});

// POST /api/runs — neuen Lauf hinzufügen
app.post("/api/runs", (req, res) => {
  const { date, distanceKm, durationMin, avgPaceMinPerKm, category, notes } = req.body;

  if (!date || !distanceKm || !durationMin || !avgPaceMinPerKm || !category) {
    res.status(400).json({ error: "Felder date, distanceKm, durationMin, avgPaceMinPerKm, category sind erforderlich" });
    return;
  }
  if (distanceKm <= 0 || durationMin <= 0 || avgPaceMinPerKm <= 0) {
    res.status(400).json({ error: "distanceKm, durationMin und avgPaceMinPerKm müssen positive Werte sein" });
    return;
  }
  const newRun: Run = {
    id: nextId++,
    date,
    distanceKm,
    durationMin,
    avgPaceMinPerKm,
    category,
    notes,
  };
  runs.push(newRun);
  res.status(201).json(newRun);
});

// PUT /api/runs/:id — Lauf aktualisieren
app.put("/api/runs/:id", (req, res) => {
  const id = Number(req.params.id);
  const index = runs.findIndex(r => r.id === id);
  if (index === -1) {
    res.status(404).json({ error: "Run nicht gefunden" });
    return;
  }
  const { date, distanceKm, durationMin, avgPaceMinPerKm, category, notes } = req.body;

  if (!date || !distanceKm || !durationMin || !avgPaceMinPerKm || !category) {
    res.status(400).json({ error: "Felder date, distanceKm, durationMin, avgPaceMinPerKm, category sind erforderlich" });
    return;
  }
  if (distanceKm <= 0 || durationMin <= 0 || avgPaceMinPerKm <= 0) {
    res.status(400).json({ error: "distanceKm, durationMin und avgPaceMinPerKm müssen positive Werte sein" });
    return;
  }
  runs[index] = { ...runs[index], date, distanceKm, durationMin, avgPaceMinPerKm, category, notes };
  res.json(runs[index]);
});

// DELETE /api/runs/:id — Lauf löschen
app.delete("/api/runs/:id", (req, res) => {
  const id = Number(req.params.id);
  const index = runs.findIndex(r => r.id === id);
  if (index === -1) {
    res.status(404).json({ error: "Run nicht gefunden" });
    return;
  }
  runs.splice(index, 1);
  res.status(204).send();
});

app.listen(PORT, () => {
  console.log(`Server läuft auf http://localhost:${PORT}`);
});