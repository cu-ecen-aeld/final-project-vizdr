# File Structure Guide

## QML Files - Which One to Edit?

### main_designer.qml Locations:

There are **two copies** of `main_designer.qml`:

```
files/
├── main_designer.qml                    ← ✅ EDIT THIS ONE (source)
└── designer-preview/
    └── build/
        └── main_designer.qml            ← ❌ DON'T EDIT (auto-generated copy)
```

### Important: Which File to Edit?

**✅ EDIT THIS:**
```
/media/vladimir/.../files/main_designer.qml
```
This is the **source file**. All your edits should go here.

**❌ DON'T EDIT THIS:**
```
/media/vladimir/.../files/designer-preview/build/main_designer.qml
```
This is a **runtime copy** that gets overwritten every time you run the preview.

## File Workflow

### For Layout Editing:

1. **Edit the source:**
   ```bash
   # Open in Qt Creator or any editor
   code files/main_designer.qml
   # OR
   qtcreator files/main_designer.qml
   ```

2. **Preview your changes:**
   ```bash
   ./preview-layout.sh
   ```
   The script automatically:
   - Copies `files/main_designer.qml` → `designer-preview/build/main_designer.qml`
   - Runs the preview application

3. **Apply to production:**
   When satisfied, copy layout changes from `main_designer.qml` to `main.qml`

## Complete File Structure

```
files/
├── Production Files (Yocto build):
│   ├── CMakeLists.txt                  # Build configuration
│   ├── main.cpp                        # Application entry point
│   ├── main.qml                        # Production QML ← final UI
│   ├── datacollector.h                 # C++ backend header
│   ├── datacollector.cpp               # C++ backend implementation
│   ├── can-visualizer.init             # SysVinit script
│   └── LICENSE                         # MIT license
│
├── Designer/Preview Files:
│   ├── main_designer.qml               # ✅ SOURCE - edit this
│   ├── preview-layout.sh               # Run preview only
│   ├── edit-layout.sh                  # Open Qt Creator + preview
│   └── designer-preview/
│       ├── CMakeLists.txt              # Preview tool build config
│       ├── preview_designer.cpp        # Preview application
│       └── build/
│           ├── designer-preview        # Compiled preview app
│           └── main_designer.qml       # ❌ COPY - auto-generated
│
├── Testing/Development:
│   ├── run-qt6.7.2.sh                  # Run production app locally
│   ├── test-threshold-color.sh         # Test color changes
│   └── build/
│       └── Qt6.7.2-Debug/              # Build directory
│           └── can-visualizer          # Compiled app
│
└── Documentation:
    ├── README.md                       # Project overview
    ├── CHANGELOG.md                    # Version history
    ├── LAYOUT_EDITING.md               # Layout editing guide
    └── FILE_STRUCTURE.md               # This file
```

## Quick Reference

### I want to...

**Preview layout changes:**
```bash
./preview-layout.sh
```

**Edit layout visually:**
```bash
./edit-layout.sh  # Opens Qt Creator + preview
```

**Test the real app:**
```bash
./run-qt6.7.2.sh
```

**Test color changes:**
```bash
./test-threshold-color.sh
```

**Build for Yocto:**
```bash
cd ../../..  # Go to project root
./build.sh
```

## Why Two Copies?

The designer preview tool runs from `designer-preview/build/` directory and loads `main_designer.qml` from the current directory.

**Why not load directly from files/?**
- The preview tool is compiled and lives in `build/`
- It expects the QML file in the same directory
- This keeps the build artifacts separate from source

**The preview scripts handle copying:**
```bash
# Both scripts do this automatically:
cp files/main_designer.qml designer-preview/build/
```

## Common Mistakes

❌ **Editing the wrong file:**
```bash
# DON'T edit this:
vim designer-preview/build/main_designer.qml
# Your changes will be lost when preview runs!
```

✅ **Edit the source:**
```bash
# DO edit this:
vim files/main_designer.qml
# Then run ./preview-layout.sh to see changes
```

❌ **Forgetting to copy changes:**
```bash
# After editing main_designer.qml, don't forget to:
# Copy your changes to main.qml for production
```

## File Synchronization

**Source of Truth:**
- `main.qml` - Production application
- `main_designer.qml` - Preview/design version (with static data)

**Auto-generated (don't commit to git):**
- `designer-preview/build/main_designer.qml`
- `build/` directories
- Compiled binaries

**See [.gitignore](.gitignore) for complete list.**
