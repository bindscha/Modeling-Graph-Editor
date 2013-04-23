package com.zeus.graph.editor;


import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;

import com.mxgraph.examples.swing.editor.BasicGraphEditor;
import com.mxgraph.examples.swing.editor.EditorMenuBar;
import com.mxgraph.util.mxResources;

import com.zeus.graph.GraphEditor;
import com.zeus.graph.Resources;
import com.zeus.graph.editor.CustomEditorActions.ExportAction;
import com.zeus.graph.editor.CustomEditorActions.ImportAction;
import com.zeus.graph.editor.CustomEditorActions.PageBackgroundAction;
import com.zeus.graph.editor.CustomEditorActions.PageResizeAction;
import com.zeus.graph.editor.CustomEditorActions.SaveAction;

public class CustomEditorMenuBar extends EditorMenuBar {

	private static final long serialVersionUID = 3606203379068939948L;

	private static final int MENU_FILE_POSITION = 0;
	private static final int MENU_EDIT_POSITION = 1;
	private static final int MENU_VIEW_POSITION = 2;
	private static final int MENU_FORMAT_POSITION = 3;
	private static final int MENU_SHAPE_POSITION = 4;
	private static final int MENU_DIAGRAM_POSITION = 5;
	private static final int MENU_OPTIONS_POSITION = 6;
	private static final int MENU_WINDOWS_POSITION = 6;
	private static final int MENU_HELP_POSITION = 8;

	private static final int SUBMENU_SAVE_POSITION = 4;
	private static final int SUBMENU_SAVE_AS_POSITION = 5;
	private static final int SUBMENU_IMPORT_POSITION = 7;
	private static final int SUBMENU_EXPORT_POSITION = 7;
	private static final int SUBMENU_EXPORT_SEPARATOR_POSITION = 7;
	private static final int SUBMENU_OUTLINE_POSITION = 0;
	private static final int SUBMENU_OUTLINE_NEW_POSITION = 1;
	private static final int SUBMENU_PAGERESIZE_POSITION = 1;
	private static final int SUBMENU_BACKGROUND_POSITION = 2;
	private static final int SUBMENU_DIAGRAMSHIT1_POSITION = 1;
	private static final int SUBMENU_DIAGRAMSHIT2_POSITION = 4;
	private static final int SUBMENU_LAYOUT_POSITION = 5;
	private static final int SUBMENU_SELECTION_POSITION = 6;
	private static final int SUBMENU_ABOUT_POSITION = 0;
	
	private static final int SUBSUBMENU_BACKGROUND_REMOVE_POSITION = 1;
	private static final int SUBSUBMENU_BACKGROUND_REMOVE_COUNT = 2;
	private static final int SUBSUBMENU_BACKGROUND_PAGEBACKGROUND_POSITION = 3;
	
	private static final int SUBSUBMENU_LAYOUT_REMOVE_POSITION = 2;
	private static final int SUBSUBMENU_LAYOUT_REMOVE_COUNT = 15;
	
	private static final int CONTEXTSUBMENU_REMOVE_POSITION1 = 5;
	private static final int CONTEXTSUBMENU_REMOVE_COUNT1 = 4;
	private static final int CONTEXTSUBMENU_REMOVE_POSITION2 = 10;
	private static final int CONTEXTSUBMENU_REMOVE_COUNT2 = 3;
	
	
	public CustomEditorMenuBar(final BasicGraphEditor editor)
	{
		super(editor);
		
		// Modify File menu
		JMenu fileMenu = getMenu(MENU_FILE_POSITION);
		fileMenu.getItem(SUBMENU_SAVE_POSITION).setAction(editor.bind(mxResources.get("save"), new SaveAction(false),
				"/com/mxgraph/examples/swing/images/save.gif"));
		fileMenu.getItem(SUBMENU_SAVE_AS_POSITION).setAction(editor.bind(mxResources.get("saveAs"), new SaveAction(true),
				"/com/mxgraph/examples/swing/images/saveas.gif"));

		fileMenu.add(new JSeparator(), SUBMENU_EXPORT_SEPARATOR_POSITION);
		
		// Add export option
		JMenuItem exportMenuItem = new javax.swing.JMenuItem(editor.bind(Resources.resource("export_menu_label"), 
				new ExportAction(), "/com/zeus/graph/images/export.gif"));
		fileMenu.add(exportMenuItem, SUBMENU_EXPORT_POSITION);
		
		// Add import option
		JMenuItem importMenuItem = new javax.swing.JMenuItem(editor.bind(Resources.resource("import_menu_label"), 
				new ImportAction(), "/com/zeus/graph/images/import.gif"));
		fileMenu.add(importMenuItem, SUBMENU_IMPORT_POSITION);
		
		// Remove some Diagram options
		JMenu diagramMenu = getMenu(MENU_DIAGRAM_POSITION);
		JMenu backgroundMenuItem = (JMenu) diagramMenu.getItem(SUBMENU_BACKGROUND_POSITION);
		JMenuItem pageBackgroundMenuItem = (JMenuItem) backgroundMenuItem.getItem(SUBSUBMENU_BACKGROUND_PAGEBACKGROUND_POSITION);
		pageBackgroundMenuItem.setAction(editor.bind(Resources.resource("page_background"), new PageBackgroundAction(),
			null));
		
		JMenuItem layoutMenuItem = getMenu(MENU_DIAGRAM_POSITION).getItem(SUBMENU_LAYOUT_POSITION);
		
		for(int i = 0; i < SUBSUBMENU_LAYOUT_REMOVE_COUNT; ++i) {
			layoutMenuItem.remove(SUBSUBMENU_LAYOUT_REMOVE_POSITION);
		}
		
		for(int i = 0; i < SUBSUBMENU_BACKGROUND_REMOVE_COUNT; ++i) {
			backgroundMenuItem.remove(SUBSUBMENU_BACKGROUND_REMOVE_POSITION);
		}
		getMenu(MENU_DIAGRAM_POSITION).remove(SUBMENU_SELECTION_POSITION);
		getMenu(MENU_DIAGRAM_POSITION).remove(SUBMENU_DIAGRAMSHIT2_POSITION);
		getMenu(MENU_DIAGRAM_POSITION).remove(SUBMENU_DIAGRAMSHIT1_POSITION);
		
		// Add Diagram options
		JMenuItem pageDimensionsMenuItem = new javax.swing.JMenuItem(editor.bind(Resources.resource("page_dimensions"), 
				new PageResizeAction(GraphEditor.DEFAULT_PAGE_WIDTH, GraphEditor.DEFAULT_PAGE_HEIGHT), null));
		diagramMenu.add(pageDimensionsMenuItem, SUBMENU_PAGERESIZE_POSITION);
		
		// Move outline option
		JMenuItem outlineMenuItem = getMenu(MENU_DIAGRAM_POSITION).getItem(SUBMENU_OUTLINE_POSITION);
		getMenu(MENU_DIAGRAM_POSITION).remove(SUBMENU_OUTLINE_POSITION);
		getMenu(MENU_VIEW_POSITION).insert(outlineMenuItem, SUBMENU_OUTLINE_NEW_POSITION);
		
		// Change text
		getMenu(MENU_HELP_POSITION).getItem(SUBMENU_ABOUT_POSITION).setText(Resources.resource("about"));
		
		// Remove some menus (do this last and in decreasing order to prevent problems with indices)
		remove(MENU_OPTIONS_POSITION);
		remove(MENU_SHAPE_POSITION);
		remove(MENU_FORMAT_POSITION);
	}
	
	/**
	 * Adds menu items to the given shape menu. This is factored out because
	 * the shape menu appears in the menubar and also in the popupmenu.
	 */
	public static void populateShapeMenu(JMenu menu, BasicGraphEditor editor)
	{
		EditorMenuBar.populateShapeMenu(menu, editor);

		
	}

	/**
	 * Adds menu items to the given format menu. This is factored out because
	 * the format menu appears in the menubar and also in the popupmenu.
	 */
	public static void populateFormatMenu(JMenu menu, BasicGraphEditor editor)
	{
		EditorMenuBar.populateFormatMenu(menu, editor);
		
		// Remove some Format menu options
		for(int i = 0; i < CONTEXTSUBMENU_REMOVE_COUNT2; ++i) {
			menu.remove(CONTEXTSUBMENU_REMOVE_POSITION2);
		}
		
		for(int i = 0; i < CONTEXTSUBMENU_REMOVE_COUNT1; ++i) {
			menu.remove(CONTEXTSUBMENU_REMOVE_POSITION1);
		}
	}
	
}
