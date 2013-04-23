package com.zeus.graph.editor;

import javax.swing.JButton;

import com.mxgraph.examples.swing.editor.BasicGraphEditor;
import com.mxgraph.examples.swing.editor.EditorToolBar;

import com.zeus.graph.editor.CustomEditorActions.ExportAction;
import com.zeus.graph.editor.CustomEditorActions.ImportAction;
import com.zeus.graph.editor.CustomEditorActions.SaveAction;

public class CustomEditorToolBar extends EditorToolBar {

	private static final long serialVersionUID = 3568136045602164392L;

	private static final int SAVE_POSITION = 2;
	private static final int IMPORT_POSITION = 4;
	private static final int EXPORT_POSITION = 4;
	
	private static final int REMOVE_POSITION = 16;
	private static final int REMOVE_COUNT = 14;
	
	public CustomEditorToolBar(final BasicGraphEditor editor, int orientation)
	{
		super(editor, orientation);
		
		JButton saveButton = (JButton)getComponentAtIndex(SAVE_POSITION);
		saveButton.setAction(editor.bind("Save", new SaveAction(false),
				"/com/mxgraph/examples/swing/images/save.gif"));
		
		JButton exportButton = new JButton(editor.bind("Export", new ExportAction(),
			"/com/zeus/graph/images/export.gif"));
		exportButton.setText(null);
		add(exportButton, EXPORT_POSITION);
		
		JButton importButton = new JButton(editor.bind("Import", new ImportAction(),
			"/com/zeus/graph/images/import.gif"));
		importButton.setText(null);
		add(importButton, IMPORT_POSITION);
		
		for(int i = 0; i < REMOVE_COUNT; ++i) {
			remove(REMOVE_POSITION);
		}
	}
	
}
