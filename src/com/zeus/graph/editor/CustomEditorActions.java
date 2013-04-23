package com.zeus.graph.editor;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashSet;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import com.mxgraph.canvas.mxICanvas;
import com.mxgraph.canvas.mxSvgCanvas;
import com.mxgraph.examples.swing.editor.BasicGraphEditor;
import com.mxgraph.examples.swing.editor.DefaultFileFilter;
import com.mxgraph.examples.swing.editor.EditorActions;
import com.mxgraph.io.mxCodec;
import com.mxgraph.io.mxGdCodec;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.util.mxCellRenderer.CanvasFactory;
import com.mxgraph.util.mxResources;
import com.mxgraph.util.mxUtils;
import com.mxgraph.util.png.mxPngEncodeParam;
import com.mxgraph.util.png.mxPngImageEncoder;
import com.mxgraph.view.mxGraph;
import com.zeus.graph.GraphEditor;
import com.zeus.graph.Resources;

public class CustomEditorActions extends EditorActions {

	@SuppressWarnings("serial")
	public static class ExportAction extends AbstractAction
	{	
		
		protected String lastDir = null;
		
		public ExportAction()
		{
		}
	
		/**
		 * Saves to GraphML format.
		 */
		protected void saveGraphMl(BasicGraphEditor _editor, String _filename) {
			if(_editor instanceof GraphEditor) {
				GraphEditor editor = (GraphEditor) _editor;
				
		        try {
		        	File file = new File(_filename);
		        	String graphId = file.getName().substring(0, file.getName().indexOf('.'));
		        	
		        	FileWriter out = new FileWriter(_filename);
		        	editor.graph().graphManager().exportGraphML(out, graphId);
				} catch (Exception e) {
					// ignored
				}
			}
		}
		
		/**
		 * Saves to XML+PNG format.
		 */
		protected void saveXmlPng(BasicGraphEditor editor, String filename,
				Color bg) throws IOException
		{
			mxGraphComponent graphComponent = editor.getGraphComponent();
			mxGraph graph = graphComponent.getGraph();
	
			// Creates the image for the PNG file
			BufferedImage image = mxCellRenderer.createBufferedImage(graph,
					null, 1, bg, graphComponent.isAntiAlias(), null,
					graphComponent.getCanvas());
	
			// Creates the URL-encoded XML data
			mxCodec codec = new mxCodec();
			String xml = URLEncoder.encode(
					mxUtils.getXml(codec.encode(graph.getModel())), "UTF-8");
			mxPngEncodeParam param = mxPngEncodeParam
					.getDefaultEncodeParam(image);
			param.setCompressedText(new String[] { "mxGraphModel", xml });
	
			// Saves as a PNG file
			FileOutputStream outputStream = new FileOutputStream(new File(
					filename));
			try
			{
				mxPngImageEncoder encoder = new mxPngImageEncoder(outputStream,
						param);
	
				if (image != null)
				{
					encoder.encode(image);
	
					editor.setModified(false);
					editor.setCurrentFile(new File(filename));
				}
				else
				{
					JOptionPane.showMessageDialog(graphComponent,
							mxResources.get("noImageData"));
				}
			}
			finally
			{
				outputStream.close();
			}
		}
	
		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			BasicGraphEditor editor = getEditor(e);
	
			if (editor != null)
			{
				mxGraphComponent graphComponent = editor.getGraphComponent();
				mxGraph graph = graphComponent.getGraph();
				FileFilter selectedFilter = null;
				DefaultFileFilter graphMlFilter = new DefaultFileFilter(".gml",
						Resources.resource("graphml_file") + " (.gml)");
				FileFilter xmlPngFilter = new DefaultFileFilter(".png",
						"PNG+XML " + mxResources.get("file") + " (.png)");
				FileFilter vmlFileFilter = new DefaultFileFilter(".html",
						"VML " + mxResources.get("file") + " (.html)");
				String filename = null;
				boolean dialogShown = false;
	
				String wd;

				if (lastDir != null)
				{
					wd = lastDir;
				}
				else if (editor.getCurrentFile() != null)
				{
					wd = editor.getCurrentFile().getParent();
				}
				else
				{
					wd = System.getProperty("user.dir");
				}

				JFileChooser fc = new JFileChooser(wd);

				// Adds the default file format
				FileFilter defaultFilter = graphMlFilter;
				fc.addChoosableFileFilter(defaultFilter);
				
				fc.addChoosableFileFilter(xmlPngFilter);

				// Adds special vector graphics formats and HTML
				fc.addChoosableFileFilter(new DefaultFileFilter(".txt",
						"Graph Drawing " + mxResources.get("file")
								+ " (.txt)"));
				fc.addChoosableFileFilter(new DefaultFileFilter(".svg",
						"SVG " + mxResources.get("file") + " (.svg)"));
				fc.addChoosableFileFilter(vmlFileFilter);
				fc.addChoosableFileFilter(new DefaultFileFilter(".html",
						"HTML " + mxResources.get("file") + " (.html)"));

				// Adds a filter for each supported image format
				Object[] imageFormats = ImageIO.getReaderFormatNames();

				// Finds all distinct extensions
				HashSet<String> formats = new HashSet<String>();

				for (int i = 0; i < imageFormats.length; i++)
				{
					String ext = imageFormats[i].toString().toLowerCase();
					formats.add(ext);
				}

				imageFormats = formats.toArray();

				for (int i = 0; i < imageFormats.length; i++)
				{
					String ext = imageFormats[i].toString();
					fc.addChoosableFileFilter(new DefaultFileFilter("."
							+ ext, ext.toUpperCase() + " "
							+ mxResources.get("file") + " (." + ext + ")"));
				}

				// Adds filter that accepts all supported image formats
				fc.addChoosableFileFilter(new DefaultFileFilter.ImageFileFilter(
						mxResources.get("allImages")));
				fc.setFileFilter(defaultFilter);
				int rc = fc.showDialog(null, Resources.resource("export_dialog_title"));
				dialogShown = true;

				if (rc != JFileChooser.APPROVE_OPTION)
				{
					return;
				}
				else
				{
					lastDir = fc.getSelectedFile().getParent();
				}

				filename = fc.getSelectedFile().getAbsolutePath();
				selectedFilter = fc.getFileFilter();

				if (selectedFilter instanceof DefaultFileFilter)
				{
					String ext = ((DefaultFileFilter) selectedFilter)
							.getExtension();

					if (!filename.toLowerCase().endsWith(ext))
					{
						filename += ext;
					}
				}

				if (new File(filename).exists()
						&& JOptionPane.showConfirmDialog(graphComponent,
								mxResources.get("overwriteExistingFile")) != JOptionPane.YES_OPTION)
				{
					return;
				}
	
				try
				{
					String ext = filename
							.substring(filename.lastIndexOf('.') + 1);
	
					if(ext.equalsIgnoreCase("gml")) {
						saveGraphMl(editor, filename);
					} else if (ext.equalsIgnoreCase("svg"))
					{
						mxSvgCanvas canvas = (mxSvgCanvas) mxCellRenderer
								.drawCells(graph, null, 1, null,
										new CanvasFactory()
										{
											public mxICanvas createCanvas(
													int width, int height)
											{
												mxSvgCanvas canvas = new mxSvgCanvas(
														mxUtils.createSvgDocument(
																width, height));
												canvas.setEmbedded(true);
	
												return canvas;
											}
	
										});
	
						mxUtils.writeFile(mxUtils.getXml(canvas.getDocument()),
								filename);
					}
					else if (selectedFilter == vmlFileFilter)
					{
						mxUtils.writeFile(mxUtils.getXml(mxCellRenderer
								.createVmlDocument(graph, null, 1, null, null)
								.getDocumentElement()), filename);
					}
					else if (ext.equalsIgnoreCase("html"))
					{
						mxUtils.writeFile(mxUtils.getXml(mxCellRenderer
								.createHtmlDocument(graph, null, 1, null, null)
								.getDocumentElement()), filename);
					}
					else if (ext.equalsIgnoreCase("mxe")
							|| ext.equalsIgnoreCase("xml"))
					{
						mxCodec codec = new mxCodec();
						String xml = mxUtils.getXml(codec.encode(graph
								.getModel()));
	
						mxUtils.writeFile(xml, filename);
	
						editor.setModified(false);
						editor.setCurrentFile(new File(filename));
					}
					else if (ext.equalsIgnoreCase("txt"))
					{
						String content = mxGdCodec.encode(graph)
								.getDocumentString();
	
						mxUtils.writeFile(content, filename);
					}
					else
					{
						Color bg = null;
	
						if ((!ext.equalsIgnoreCase("gif") && !ext
								.equalsIgnoreCase("png"))
								|| JOptionPane.showConfirmDialog(
										graphComponent, mxResources
												.get("transparentBackground")) != JOptionPane.YES_OPTION)
						{
							bg = graphComponent.getBackground();
						}
	
						if (selectedFilter == xmlPngFilter
								|| (editor.getCurrentFile() != null
										&& ext.equalsIgnoreCase("png") && !dialogShown))
						{
							saveXmlPng(editor, filename, bg);
						}
						else
						{
							BufferedImage image = mxCellRenderer
									.createBufferedImage(graph, null, 1, bg,
											graphComponent.isAntiAlias(), null,
											graphComponent.getCanvas());
	
							if (image != null)
							{
								ImageIO.write(image, ext, new File(filename));
							}
							else
							{
								JOptionPane.showMessageDialog(graphComponent,
										mxResources.get("noImageData"));
							}
						}
					}
				}
				catch (Throwable ex)
				{
					ex.printStackTrace();
					JOptionPane.showMessageDialog(graphComponent,
							ex.toString(), mxResources.get("error"),
							JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}
	
	@SuppressWarnings("serial")
	public static class SaveAction extends AbstractAction
	{
		/**
		 * 
		 */
		protected boolean showDialog;

		/**
		 * 
		 */
		protected String lastDir = null;

		/**
		 * 
		 */
		public SaveAction(boolean showDialog)
		{
			this.showDialog = showDialog;
		}
		
		protected void saveMxe(BasicGraphEditor editor, String filename,
				Color bg) throws IOException
		{
			mxGraphComponent graphComponent = editor.getGraphComponent();
		}

		/**
		 * Saves XML+PNG format.
		 */
		protected void saveXmlPng(BasicGraphEditor editor, String filename,
				Color bg) throws IOException
		{
			mxGraphComponent graphComponent = editor.getGraphComponent();
			mxGraph graph = graphComponent.getGraph();

			// Creates the image for the PNG file
			BufferedImage image = mxCellRenderer.createBufferedImage(graph,
					null, 1, bg, graphComponent.isAntiAlias(), null,
					graphComponent.getCanvas());

			// Creates the URL-encoded XML data
			mxCodec codec = new mxCodec();
			String xml = URLEncoder.encode(
					mxUtils.getXml(codec.encode(graph.getModel())), "UTF-8");
			mxPngEncodeParam param = mxPngEncodeParam
					.getDefaultEncodeParam(image);
			param.setCompressedText(new String[] { "mxGraphModel", xml });

			// Saves as a PNG file
			FileOutputStream outputStream = new FileOutputStream(new File(
					filename));
			try
			{
				mxPngImageEncoder encoder = new mxPngImageEncoder(outputStream,
						param);

				if (image != null)
				{
					encoder.encode(image);

					editor.setModified(false);
					editor.setCurrentFile(new File(filename));
				}
				else
				{
					JOptionPane.showMessageDialog(graphComponent,
							mxResources.get("noImageData"));
				}
			}
			finally
			{
				outputStream.close();
			}
		}

		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			BasicGraphEditor editor = getEditor(e);

			if (editor != null)
			{
				mxGraphComponent graphComponent = editor.getGraphComponent();
				mxGraph graph = graphComponent.getGraph();
				FileFilter selectedFilter = null;
				DefaultFileFilter mxeFilter = new DefaultFileFilter(".mxe",
						Resources.resource("graph_markup_file") + " " + " (.mxe)");
				/*DefaultFileFilter xmlPngFilter = new DefaultFileFilter(".png",
						"PNG+XML " + mxResources.get("file") + " (.png)");
				FileFilter vmlFileFilter = new DefaultFileFilter(".html",
						"VML " + mxResources.get("file") + " (.html)");*/
				String filename = null;
				boolean dialogShown = false;

				if (showDialog || editor.getCurrentFile() == null)
				{
					String wd;

					if (lastDir != null)
					{
						wd = lastDir;
					}
					else if (editor.getCurrentFile() != null)
					{
						wd = editor.getCurrentFile().getParent();
					}
					else
					{
						wd = System.getProperty("user.dir");
					}

					JFileChooser fc = new JFileChooser(wd);

					// Adds the default file format
					DefaultFileFilter defaultFilter = mxeFilter;
					fc.addChoosableFileFilter(defaultFilter);
					
					/*fc.addChoosableFileFilter(xmlPngFilter);*/

					// Adds the default file format
					/*FileFilter defaultFilter = xmlPngFilter;
					fc.addChoosableFileFilter(defaultFilter);*/

					// Adds special vector graphics formats and HTML
					/*fc.addChoosableFileFilter(new DefaultFileFilter(".mxe",
							"mxGraph Editor " + mxResources.get("file")
									+ " (.mxe)"));
					fc.addChoosableFileFilter(new DefaultFileFilter(".txt",
							"Graph Drawing " + mxResources.get("file")
									+ " (.txt)"));
					fc.addChoosableFileFilter(new DefaultFileFilter(".svg",
							"SVG " + mxResources.get("file") + " (.svg)"));
					fc.addChoosableFileFilter(vmlFileFilter);
					fc.addChoosableFileFilter(new DefaultFileFilter(".html",
							"HTML " + mxResources.get("file") + " (.html)"));

					// Adds a filter for each supported image format
					Object[] imageFormats = ImageIO.getReaderFormatNames();

					// Finds all distinct extensions
					HashSet<String> formats = new HashSet<String>();

					for (int i = 0; i < imageFormats.length; i++)
					{
						String ext = imageFormats[i].toString().toLowerCase();
						formats.add(ext);
					}

					imageFormats = formats.toArray();

					for (int i = 0; i < imageFormats.length; i++)
					{
						String ext = imageFormats[i].toString();
						fc.addChoosableFileFilter(new DefaultFileFilter("."
								+ ext, ext.toUpperCase() + " "
								+ mxResources.get("file") + " (." + ext + ")"));
					}

					// Adds filter that accepts all supported image formats
					fc.addChoosableFileFilter(new DefaultFileFilter.ImageFileFilter(
							mxResources.get("allImages")));*/
					fc.setFileFilter(defaultFilter);
					int rc = fc.showDialog(null, mxResources.get("save"));
					dialogShown = true;

					if (rc != JFileChooser.APPROVE_OPTION)
					{
						return;
					}
					else
					{
						lastDir = fc.getSelectedFile().getParent();
					}

					filename = fc.getSelectedFile().getAbsolutePath();
					selectedFilter = fc.getFileFilter();

					if (selectedFilter instanceof DefaultFileFilter)
					{
						String ext = ((DefaultFileFilter) selectedFilter)
								.getExtension();

						if (!filename.toLowerCase().endsWith(ext))
						{
							filename += ext;
						}
					}

					if (new File(filename).exists()
							&& JOptionPane.showConfirmDialog(graphComponent,
									mxResources.get("overwriteExistingFile")) != JOptionPane.YES_OPTION)
					{
						return;
					}
				}
				else
				{
					filename = editor.getCurrentFile().getAbsolutePath();
				}

				try
				{
					String ext = filename
							.substring(filename.lastIndexOf('.') + 1);

					if (ext.equalsIgnoreCase("svg"))
					{
						mxSvgCanvas canvas = (mxSvgCanvas) mxCellRenderer
								.drawCells(graph, null, 1, null,
										new CanvasFactory()
										{
											public mxICanvas createCanvas(
													int width, int height)
											{
												mxSvgCanvas canvas = new mxSvgCanvas(
														mxUtils.createSvgDocument(
																width, height));
												canvas.setEmbedded(true);

												return canvas;
											}

										});

						mxUtils.writeFile(mxUtils.getXml(canvas.getDocument()),
								filename);
					}
					/*else if (selectedFilter == vmlFileFilter)
					{
						mxUtils.writeFile(mxUtils.getXml(mxCellRenderer
								.createVmlDocument(graph, null, 1, null, null)
								.getDocumentElement()), filename);
					}*/
					else if (ext.equalsIgnoreCase("html"))
					{
						mxUtils.writeFile(mxUtils.getXml(mxCellRenderer
								.createHtmlDocument(graph, null, 1, null, null)
								.getDocumentElement()), filename);
					}
					else if (ext.equalsIgnoreCase("mxe")
							|| ext.equalsIgnoreCase("xml"))
					{
						mxCodec codec = new mxCodec();
						String xml = mxUtils.getXml(codec.encode(graph
								.getModel()));

						mxUtils.writeFile(xml, filename);

						editor.setModified(false);
						editor.setCurrentFile(new File(filename));
					}
					else if (ext.equalsIgnoreCase("txt"))
					{
						String content = mxGdCodec.encode(graph)
								.getDocumentString();

						mxUtils.writeFile(content, filename);
					}
					else
					{
						Color bg = null;

						if ((!ext.equalsIgnoreCase("gif") && !ext
								.equalsIgnoreCase("png"))
								|| JOptionPane.showConfirmDialog(
										graphComponent, mxResources
												.get("transparentBackground")) != JOptionPane.YES_OPTION)
						{
							bg = graphComponent.getBackground();
						}

						if (selectedFilter == mxeFilter
								|| (editor.getCurrentFile() != null
										&& ext.equalsIgnoreCase("mxe") && !dialogShown))
						{
							saveMxe(editor, filename, bg);
						}
						else
						{
							BufferedImage image = mxCellRenderer
									.createBufferedImage(graph, null, 1, bg,
											graphComponent.isAntiAlias(), null,
											graphComponent.getCanvas());

							if (image != null)
							{
								ImageIO.write(image, ext, new File(filename));
							}
							else
							{
								JOptionPane.showMessageDialog(graphComponent,
										mxResources.get("noImageData"));
							}
						}
					}
				}
				catch (Throwable ex)
				{
					ex.printStackTrace();
					JOptionPane.showMessageDialog(graphComponent,
							ex.toString(), mxResources.get("error"),
							JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}
	
	@SuppressWarnings("serial")
	public static class ImportAction extends AbstractAction
	{
		/**
		 * 
		 */
		protected String lastDir = null;

		/**
		 * 
		 */
		public ImportAction()
		{
		}
		
		protected void importGml(BasicGraphEditor _editor, String _filename) throws IOException
		{
			if(_editor instanceof GraphEditor) {
				GraphEditor editor = (GraphEditor) _editor;
				
		        try {
		        	FileReader in = new FileReader(_filename);
		        	editor.graph().graphManager().importGraphML(in);
		        	editor.graphLayout("verticalHierarchical", true).actionPerformed(null);
				} catch (Exception e) {
					// ignored
				}
			}
		}

		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			BasicGraphEditor editor = getEditor(e);

			if (editor != null)
			{
				mxGraphComponent graphComponent = editor.getGraphComponent();
				mxGraph graph = graphComponent.getGraph();
				FileFilter selectedFilter = null;
				DefaultFileFilter gmlFilter = new DefaultFileFilter(".gml",
						Resources.resource("graphml_file") + " (.gml)");
				/*DefaultFileFilter xmlPngFilter = new DefaultFileFilter(".png",
						"PNG+XML " + mxResources.get("file") + " (.png)");
				FileFilter vmlFileFilter = new DefaultFileFilter(".html",
						"VML " + mxResources.get("file") + " (.html)");*/
				String filename = null;
				boolean dialogShown = false;

				if (editor.getCurrentFile() == null)
				{
					String wd;

					if (lastDir != null)
					{
						wd = lastDir;
					}
					else if (editor.getCurrentFile() != null)
					{
						wd = editor.getCurrentFile().getParent();
					}
					else
					{
						wd = System.getProperty("user.dir");
					}

					JFileChooser fc = new JFileChooser(wd);

					// Adds the default file format
					DefaultFileFilter defaultFilter = gmlFilter;
					fc.addChoosableFileFilter(defaultFilter);
					
					/*fc.addChoosableFileFilter(xmlPngFilter);*/

					// Adds the default file format
					/*FileFilter defaultFilter = xmlPngFilter;
					fc.addChoosableFileFilter(defaultFilter);*/

					// Adds special vector graphics formats and HTML
					/*fc.addChoosableFileFilter(new DefaultFileFilter(".mxe",
							"mxGraph Editor " + mxResources.get("file")
									+ " (.mxe)"));
					fc.addChoosableFileFilter(new DefaultFileFilter(".txt",
							"Graph Drawing " + mxResources.get("file")
									+ " (.txt)"));
					fc.addChoosableFileFilter(new DefaultFileFilter(".svg",
							"SVG " + mxResources.get("file") + " (.svg)"));
					fc.addChoosableFileFilter(vmlFileFilter);
					fc.addChoosableFileFilter(new DefaultFileFilter(".html",
							"HTML " + mxResources.get("file") + " (.html)"));

					// Adds a filter for each supported image format
					Object[] imageFormats = ImageIO.getReaderFormatNames();

					// Finds all distinct extensions
					HashSet<String> formats = new HashSet<String>();

					for (int i = 0; i < imageFormats.length; i++)
					{
						String ext = imageFormats[i].toString().toLowerCase();
						formats.add(ext);
					}

					imageFormats = formats.toArray();

					for (int i = 0; i < imageFormats.length; i++)
					{
						String ext = imageFormats[i].toString();
						fc.addChoosableFileFilter(new DefaultFileFilter("."
								+ ext, ext.toUpperCase() + " "
								+ mxResources.get("file") + " (." + ext + ")"));
					}

					// Adds filter that accepts all supported image formats
					fc.addChoosableFileFilter(new DefaultFileFilter.ImageFileFilter(
							mxResources.get("allImages")));*/
					fc.setFileFilter(defaultFilter);
					int rc = fc.showDialog(null, Resources.resource("import_dialog_title"));
					dialogShown = true;

					if (rc != JFileChooser.APPROVE_OPTION)
					{
						return;
					}
					else
					{
						lastDir = fc.getSelectedFile().getParent();
					}

					filename = fc.getSelectedFile().getAbsolutePath();
					selectedFilter = fc.getFileFilter();

					if (selectedFilter instanceof DefaultFileFilter)
					{
						String ext = ((DefaultFileFilter) selectedFilter)
								.getExtension();

						if (!filename.toLowerCase().endsWith(ext))
						{
							filename += ext;
						}
					}
				}
				else
				{
					filename = editor.getCurrentFile().getAbsolutePath();
				}

				try
				{
					String ext = filename
							.substring(filename.lastIndexOf('.') + 1);

					/*if (ext.equalsIgnoreCase("svg"))
					{
						mxSvgCanvas canvas = (mxSvgCanvas) mxCellRenderer
								.drawCells(graph, null, 1, null,
										new CanvasFactory()
										{
											public mxICanvas createCanvas(
													int width, int height)
											{
												mxSvgCanvas canvas = new mxSvgCanvas(
														mxUtils.createSvgDocument(
																width, height));
												canvas.setEmbedded(true);

												return canvas;
											}

										});

						mxUtils.writeFile(mxUtils.getXml(canvas.getDocument()),
								filename);
					}
					/*else if (selectedFilter == vmlFileFilter)
					{
						mxUtils.writeFile(mxUtils.getXml(mxCellRenderer
								.createVmlDocument(graph, null, 1, null, null)
								.getDocumentElement()), filename);
					}*/
					/*else if (ext.equalsIgnoreCase("html"))
					{
						mxUtils.writeFile(mxUtils.getXml(mxCellRenderer
								.createHtmlDocument(graph, null, 1, null, null)
								.getDocumentElement()), filename);
					}
					else if (ext.equalsIgnoreCase("mxe")
							|| ext.equalsIgnoreCase("xml"))
					{
						mxCodec codec = new mxCodec();
						String xml = mxUtils.getXml(codec.encode(graph
								.getModel()));

						mxUtils.writeFile(xml, filename);

						editor.setModified(false);
						editor.setCurrentFile(new File(filename));
					}
					else if (ext.equalsIgnoreCase("txt"))
					{
						String content = mxGdCodec.encode(graph)
								.getDocumentString();

						mxUtils.writeFile(content, filename);
					}
					else
					{*/
						/*Color bg = null;*/

						/*if ((!ext.equalsIgnoreCase("gif") && !ext
								.equalsIgnoreCase("png"))
								|| JOptionPane.showConfirmDialog(
										graphComponent, mxResources
												.get("transparentBackground")) != JOptionPane.YES_OPTION)
						{
							bg = graphComponent.getBackground();
						}*/

						if (selectedFilter == gmlFilter
								|| (editor.getCurrentFile() != null
										&& ext.equalsIgnoreCase("gml") && !dialogShown))
						{
							if(editor instanceof GraphEditor) {
								GraphEditor customEditor = (GraphEditor) editor;
								
								if(customEditor.graph().getChildCells(customEditor.graph().getDefaultParent()).length == 0 || JOptionPane.showConfirmDialog(graphComponent, Resources.resource("confirm_import")) == JOptionPane.YES_OPTION) {
									importGml(editor, filename);
								}
							}
						}
						/*else
						{
							BufferedImage image = mxCellRenderer
									.createBufferedImage(graph, null, 1, bg,
											graphComponent.isAntiAlias(), null,
											graphComponent.getCanvas());

							if (image != null)
							{
								ImageIO.write(image, ext, new File(filename));
							}
							else
							{
								JOptionPane.showMessageDialog(graphComponent,
										mxResources.get("noImageData"));
							}
						}*/
					/*}*/
				}
				catch (Throwable ex)
				{
					ex.printStackTrace();
					JOptionPane.showMessageDialog(graphComponent,
							ex.toString(), mxResources.get("error"),
							JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}
	
	@SuppressWarnings("serial")
	public static class PageBackgroundAction extends AbstractAction
	{
		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			if (e.getSource() instanceof mxGraphComponent)
			{
				mxGraphComponent graphComponent = (mxGraphComponent) e
						.getSource();
				Color newColor = JColorChooser.showDialog(graphComponent,
						mxResources.get("pageBackground"), null);

				if (newColor != null)
				{
					graphComponent.setPageBackgroundColor(newColor);
					graphComponent.setPageShadowColor(newColor);
				}

				// Forces a repaint of the component
				graphComponent.repaint();
			}
		}
	}
	
	@SuppressWarnings("serial")
	public static class PageResizeAction extends AbstractAction
	{
		/**
		 * 
		 */
		protected double width, height;

		/**
		 * 
		 */
		public PageResizeAction(double width, double height)
		{
			this.width = width;
			this.height = height;
		}

		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			if (e.getSource() instanceof mxGraphComponent)
			{
				mxGraphComponent graphComponent = (mxGraphComponent) e
						.getSource();
				
				double width = this.width;
				double height = this.height;

				String value = (String) JOptionPane.showInputDialog(
						graphComponent, Resources.resource("specify_page_dimensions"),
						Resources.resource("page_dimensions"),
						JOptionPane.PLAIN_MESSAGE, null, null, width + " x " + height);

				if (value != null && value.indexOf('x') > 0)
				{
					int xPosition = value.indexOf('x');
					width = Double.parseDouble(value.substring(0, xPosition - 1));
					height = Double.parseDouble(value.substring(xPosition + 1));
				}

				System.out.println("New values: " + width + " x " + height);
				
				if (width > 0 && height > 0)
				{
					PageFormat pageFormat = graphComponent.getPageFormat();
					Paper paper = new Paper();
					paper.setSize(width, height);
					pageFormat.setPaper(paper);
					graphComponent.setPageFormat(pageFormat);
					graphComponent.refresh();
					
					new ZoomPolicyAction(mxGraphComponent.ZOOM_POLICY_PAGE).actionPerformed(e);
					
					this.width = width;
					this.height = height;
				}
			}
		}
	}
	
}
