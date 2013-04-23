package com.zeus.graph.editor;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Frame;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.mxgraph.examples.swing.editor.EditorAboutFrame;
import com.zeus.graph.Resources;

public class CustomEditorAboutFrame extends EditorAboutFrame {

	private static final long serialVersionUID = -9221655635722329534L;

	private static final int TITLE_PANEL_POSITION = 0;
	private static final int CONTENT_PANEL_POSITION = 1;
	
	public CustomEditorAboutFrame(Frame owner)
	{
		super(owner);
		
		// Set up frame title
		String title = Resources.arg(Resources.resource("about_frame_title"), Resources.fullToolName());
		setTitle(title);
		
		// Set up title panel
		JPanel titlePanel = ((JPanel)getContentPane().getComponent(TITLE_PANEL_POSITION));
		titlePanel.removeAll();
		
		JLabel titleLabel = new JLabel(title);
		titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD));
		titleLabel.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));
		titleLabel.setOpaque(false);
		titlePanel.add(titleLabel, BorderLayout.NORTH);

		String url = Resources.arg(Resources.resource("about_website_url"), Resources.toolURL());
		JLabel subtitleLabel = new JLabel(url);
		subtitleLabel.setBorder(BorderFactory.createEmptyBorder(4, 18, 0, 0));
		subtitleLabel.setOpaque(false);
		titlePanel.add(subtitleLabel, BorderLayout.CENTER);
		
		
		// Set up content panel
		JPanel contentPanel = ((JPanel)getContentPane().getComponent(CONTENT_PANEL_POSITION));
		contentPanel.removeAll();
		
		contentPanel.add(new JLabel(Resources.resource("about_frame_description")));
		contentPanel.add(new JLabel(""));
		
		String versionNotice = Resources.arg(Resources.resource("version_notice"), Resources.toolVersion());
		contentPanel.add(new JLabel(versionNotice));
		
		String copyrightNotice = Resources.arg(Resources.resource("copyright_notice"), Resources.companyName());
		contentPanel.add(new JLabel(copyrightNotice));
		
		String authorsNotice = Resources.arg(Resources.resource("authors_notice"), Resources.authors());
		contentPanel.add(new JLabel(authorsNotice));
		contentPanel.add(new JLabel(" "));
		contentPanel.add(new JLabel(Resources.resource("copyright_acknowledgements")));
	}
	
}
