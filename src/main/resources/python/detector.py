#!/usr/bin/env python
# -*- coding: utf-8 -*-

import os
import sys
import json
from pathlib import Path
from ultralytics import YOLO
from PIL import Image

# -----------------------------
# Paths (override with YOLO_MODEL_DIR env)
# -----------------------------
BASE_DIR = Path(__file__).resolve().parent
MODEL_DIR = Path(os.environ.get("YOLO_MODEL_DIR", BASE_DIR / "../model")).resolve()

def die(msg: str, code: int = 1):
    print(json.dumps({"error": msg}))
    sys.exit(code)

if not MODEL_DIR.exists():
    die(f"Model directory not found: {MODEL_DIR}", 1)

# -----------------------------
# Load models
# -----------------------------
def load_models():
    files = {
        "plant": MODEL_DIR / "best.pt",
        "leaf": MODEL_DIR / "best_leaf.pt",
        "animal": MODEL_DIR / "best_animal.pt",
    }
    missing = [name for name, p in files.items() if not p.exists()]
    if missing:
        die(f"Missing model files for: {', '.join(missing)} in {MODEL_DIR}", 1)

    try:
        return {name: YOLO(str(path)) for name, path in files.items()}
    except Exception as e:
        die(f"Error loading models: {e}", 1)

MODELS = load_models()

# -----------------------------
# Detection
# -----------------------------
def detect(model_name: str, image_path: str, conf: float = 0.25, imgsz: int = 640):
    if model_name not in MODELS:
        return {"error": f"Unknown model: {model_name}"}

    try:
        img = Image.open(image_path).convert("RGB")
    except Exception as e:
        return {"error": f"Could not read image {image_path}: {e}"}

    try:
        results = MODELS[model_name].predict(
            img, conf=float(conf), imgsz=int(imgsz), verbose=False
        )

        detections = []
        for r in results:
            for b in r.boxes:
                x1, y1, x2, y2 = [float(v) for v in b.xyxy[0].tolist()]
                conf_v = float(b.conf[0])
                cls_id = int(b.cls[0])
                label = MODELS[model_name].names.get(cls_id, str(cls_id))
                detections.append({
                    "label": label,
                    "confidence": conf_v,
                    "bbox": [x1, y1, x2, y2]
                })
        return detections
    except Exception as e:
        return {"error": f"Prediction failed: {e}"}

# -----------------------------
# CLI
# -----------------------------
if __name__ == "__main__":
    if len(sys.argv) < 3:
        die("Usage: detector.py <plant|leaf|animal> <image_path> [conf] [imgsz]", 1)

    model_name = sys.argv[1]
    img_path = sys.argv[2]
    conf = float(sys.argv[3]) if len(sys.argv) > 3 else float(os.environ.get("YOLO_CONF", 0.25))
    imgsz = int(sys.argv[4]) if len(sys.argv) > 4 else int(os.environ.get("YOLO_IMGSZ", 640))

    result = detect(model_name, img_path, conf=conf, imgsz=imgsz)
    print(json.dumps(result))
    if isinstance(result, dict) and "error" in result:
        sys.exit(2)