# FreeMind CE - Component Inventory

> Generated: 2026-03-10 | Scan Level: Deep

## UI Components

### Map Canvas & Rendering

| Component | Class | Purpose |
|---|---|---|
| Map Canvas | `MapView` | Main scrollable map display, zoom, autoscroll, print |
| Node Renderer | `NodeView` | Individual node display, drag state, folding |
| Root Node | `RootMainView` | Root node visual style |
| Fork Node | `ForkMainView` | Fork-style node visual |
| Bubble Node | `BubbleMainView` | Bubble-style node visual |
| Cloud | `CloudView` | Node grouping cloud visual |
| Fold Toggle | `NodeFoldingComponent` | Collapse/expand icon |
| Image | `MultipleImage` | Image rendering in nodes |

### Edge Rendering

| Component | Class | Style |
|---|---|---|
| Linear Edge | `LinearEdgeView` | Straight line connection |
| Sharp Linear | `SharpLinearEdgeView` | Sharp straight connection |
| Bezier Edge | `BezierEdgeView` | Curved connection |
| Sharp Bezier | `SharpBezierEdgeView` | Sharp curved connection |

### Node Layouts

| Layout | Class | Use |
|---|---|---|
| Left Layout | `LeftNodeViewLayout` | Left-side child positioning |
| Right Layout | `RightNodeViewLayout` | Right-side child positioning |
| Vertical Root | `VerticalRootNodeViewLayout` | Vertical root arrangement |
| Map Layout | `MindMapLayout` | Custom LayoutManager |

### Editing Components

| Component | Class | Purpose |
|---|---|---|
| Inline Editor | `EditNodeTextField` | In-place text editing |
| Dialog Editor | `EditNodeDialog` | Dialog-based rich editing |
| External Editor | `EditNodeExternalApplication` | Open in external app |

### Toolbar & Menus

| Component | Class | Purpose |
|---|---|---|
| Menu Bar | `MenuBar` | Application menus from XML |
| Main Toolbar | `MainToolBar` | Primary action toolbar |
| Toolbar Base | `FreeMindToolBar` | Toolbar management base |
| Popup Menu | `FreeMindPopupMenu` | Context menus |
| Menu Holder | `StructuredMenuHolder` | Menu structure container |

### Color Components

| Component | Class | Purpose |
|---|---|---|
| Color Swatch | `ColorSwatch` | Color picker widget |
| Color Combo | `JColorCombo` | Color dropdown selector |
| Color Pair | `ColorPair` | Foreground/background pair |

### Filter UI

| Component | Class | Purpose |
|---|---|---|
| Filter Controller | `FilterController` | Filter management |
| Default Filter | `DefaultFilter` | Filter engine |
| Node Contains | `NodeContainsCondition` | Text search filter |
| Attribute Compare | `AttributeCompareCondition` | Attribute value filter |
| Icon Contains | `IconContainedCondition` | Icon-based filter |
| Conjunct/Disjunct | `ConjunctConditions` / `DisjunctConditions` | AND/OR combinators |

### Print Preview

| Component | Class | Purpose |
|---|---|---|
| Preview Dialog | `PreviewDialog` | Print preview window |
| Preview Panel | `Preview` | Preview rendering surface |
| Zoom Action | `ZoomAction` | Print preview zoom |

### Preferences

| Component | Class | Purpose |
|---|---|---|
| Option Panel | `OptionPanel` | Preferences tabbed dialog |
| Property Listener | `FreemindPropertyListener` | Preference change observer |

## Event Handlers

| Handler | Class | Events |
|---|---|---|
| Mouse Motion | `NodeMouseMotionListener` | Hover, cursor changes |
| Node Motion | `NodeMotionListener` | Node drag movement |
| Key Listener | `NodeKeyListener` | Keyboard shortcuts |
| Drag Listener | `NodeDragListener` | Drag initiation |
| Drop Listener | `NodeDropListener` | Drop target handling |
| Map Mouse | `MapMouseMotionListener` | Canvas mouse events |
| Mouse Wheel | `MapMouseWheelListener` | Zoom via scroll |

## State Management Components

| Component | Class | Purpose |
|---|---|---|
| Map Module Manager | `MapModuleManager` | Multiple open maps |
| Map Module | `MapModule` | Model+View+Controller bundle |
| Last State | `LastStateStorageManagement` | Window position persistence |
| Recent Files | `LastOpenedList` | Recently opened files |
| Zoom Listener | `ZoomListener` | Zoom level tracking |
| Undo Handler | `UndoActionHandler` | Undo/redo stack management |
| Action Pair | `ActionPair` | Forward + backward action |

## Plugin Components

### SVG/PDF Export
- `ExportVectorGraphic` - Base export logic
- `ExportSvg` - SVG export action
- `ExportPdf` - PDF export action
- `ExportPdfDialog` - PDF options dialog
- `ExportPdfPapers` - Paper size definitions

### Scripting Engine
- `ScriptingEngine` - Groovy script executor
- `ScriptEditor` - Script editor dialog
- `ScriptEditorPanel` - Editor UI panel
- `SignedScriptHandler` - Script signing/verification
- `ScriptingSecurityManager` - Sandbox security

### Map Viewer
- `MapDialog` - Map display window
- `FreeMindMapController` - Map controls
- `JCursorMapViewer` - Custom map viewer
- `MapNodePositionHolder` - Node geolocation
- `MapMarkerLocation` - Location markers
- `TileImage` - Map tile caching

### Search
- `SearchControllerHook` - Hook adapter
- `Search` - Search logic
- `SearchViewPanel` - Search UI
- `FileSearchModel` - File indexing (Lucene)

### LaTeX
- `LatexNodeHook` - LaTeX rendering hook
- `JZoomedHotEqn` - Equation renderer with zoom

### Help
- `FreemindHelpStarter` - Help system integrator

### Collaboration (Socket)
- TCP socket-based real-time collaboration

## Extension Framework

| Component | Class | Purpose |
|---|---|---|
| Hook Base | `HookAdapter` | Base hook implementation |
| Mode Hook | `ModeControllerHookAdapter` | Mode-level extensions |
| Node Hook | `NodeHookAdapter` | Node-level extensions |
| Permanent Hook | `PermanentNodeHookAdapter` | Persistent extensions |
| Hook Factory | `HookFactory` | Plugin instantiation |
| Hook Registration | `HookRegistration` | Registration metadata |
| Hook Descriptor | `HookDescriptorBase` | Plugin descriptor |
