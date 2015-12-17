package com.main.MainWindow;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JButton;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JTable;

import com.HashGeneratorUtils.HashGeneratorUtils;
import com.deduplication.Chunk;
import com.deduplication.ChunkIndexTable;
import com.deduplication.ChunkedFile;
import com.deduplication.FileProfile;
import com.deduplication.FileChunkMappings;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.table.DefaultTableModel;
import javax.swing.JFileChooser;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * @author Hua Li
 * 
 */
public class MainWindow {

	private JFrame frame;
	private JTable table;
	private JPanel panelBanner;
	private JPanel panelButton;
	private JButton btnUpload;
	private JButton btnDownload;
	private JButton btnRemove;
	private JButton btnMove;
	private JButton btnNewFolder;
	private JFileChooser fileOpenChooser;
	private JFileChooser fileSaveChooser;
	private JOptionPane messagebox;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FileChunkMappings mp = new FileChunkMappings();
					MainWindow window = new MainWindow();
					window.frame.setVisible(true);
					ImageIcon mianIcon = new ImageIcon("images/cloud.png");
					window.frame.setIconImage(mianIcon.getImage());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainWindow() {
		initialize();
		ChunkIndexTable.getInstance();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		// frame.setExtendedState(Frame.MAXIMIZED_BOTH);
		frame.setBounds(100, 100, 960, 600);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation(dim.width / 2 - frame.getSize().width / 2, dim.height
				/ 2 - frame.getSize().height / 2);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		frame.addWindowListener(new WindowListener() {

			@Override
			public void windowClosed(WindowEvent arg0) {
				// TODO Auto-generated method stub
				ChunkIndexTable.getInstance().Save();
			}

			@Override
			public void windowActivated(WindowEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowClosing(WindowEvent arg0) {
				// TODO Auto-generated method stub
				ChunkIndexTable.getInstance().Save();
			}

			@Override
			public void windowDeactivated(WindowEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowDeiconified(WindowEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowIconified(WindowEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowOpened(WindowEvent arg0) {
				// TODO Auto-generated method stub

			}
		});
		table = CreateTable();
		table.getColumnModel().getColumn(0).setMinWidth(0);
		table.getColumnModel().getColumn(0).setMaxWidth(0);
		table.getColumnModel().getColumn(0).setWidth(0);
		initTableData();

		JScrollPane scrollPane = new JScrollPane(table);
		frame.getContentPane().add(scrollPane);// Using JScrollPane to contain
												// the table, otherwise the
												// header of table will not show

		JButton button = new JButton("New button");
		scrollPane.setColumnHeaderView(button);

		BufferedImage myPicture;

		try {
			myPicture = ImageIO.read(new File("images/cloud.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		JLabel labelBanner = new JLabel(new ImageIcon(""));// with no imange
		labelBanner.setAlignmentX(Component.LEFT_ALIGNMENT);

		panelBanner = new JPanel();
		panelBanner.setLayout(new BoxLayout(panelBanner, BoxLayout.Y_AXIS));
		panelBanner.add(labelBanner);
		panelBanner.setAlignmentX(Component.LEFT_ALIGNMENT);

		panelButton = new JPanel();
		panelButton.setLayout(new GridLayout(1, 6));

		frame.getContentPane().add(panelBanner, BorderLayout.NORTH);
		panelBanner.add(panelButton);

		btnUpload = CreateButton(" Upload", "images/btn_upload.png");

		btnUpload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				fileOpenChooser = new JFileChooser("C:/");
				int returnVal = fileOpenChooser.showOpenDialog(frame);

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fileOpenChooser.getSelectedFile();
					FileProfile fpro = new FileProfile(file);
					try {
						HashGeneratorUtils.genrateMD5(file, fpro);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					// This is where a real application would open the file.
					Object[] row = { fpro.getId(), fpro.getName(),
							fpro.getSize() + " K", fpro.getUploadDate() };
					((DefaultTableModel) table.getModel()).addRow(row);
				} else {
				}
			}
		});
		panelButton.add(btnUpload);

		btnDownload = CreateButton(" Download", "images/btn_download.png");
		btnDownload.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				int selectedRow = table.getSelectedRow();
				if (selectedRow >= 0) {
					String fileId = (String) table.getValueAt(
							table.getSelectedRow(), 0);
					String fileName = (String) table.getValueAt(
							table.getSelectedRow(), 1);
					// Retrieve chunks based on fileName and prompt to user to
					// save
					fileSaveChooser = new JFileChooser();
					fileSaveChooser.setSelectedFile(new File("D:/" + fileName));

					int returnVal = fileSaveChooser.showSaveDialog(frame);

					if (returnVal == JFileChooser.APPROVE_OPTION) {
						File fileToSave = fileSaveChooser.getSelectedFile();
						if (!fileToSave.exists()) {
							try {
								fileToSave.createNewFile();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

						try {
							FileOutputStream fos = new FileOutputStream(
									fileToSave);
							fos.write(ChunkedFile.retriveFileData(fileId));
							fos.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					} else {
					}
				} else {
					messagebox.showMessageDialog(frame, "No file selected!");
				}
			}
		});
		panelButton.add(btnDownload);
		// messagebox.showMessageDialog(frame,"download successfully!");

		btnNewFolder = CreateButton(" New Folder", "images/btn_newfolder.png");
		panelButton.add(btnNewFolder);

		btnRemove = CreateButton(" Remove", "images/btn_remove.png");
		btnRemove.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				int selectedRow = table.getSelectedRow();
				if (selectedRow >= 0) {
					String fileId = (String) table.getValueAt(selectedRow, 0);
					String fileName = (String) table.getValueAt(selectedRow, 1);
					ArrayList<Chunk> chunklist = FileChunkMappings
							.getChunksByFile(fileId);
					ChunkIndexTable.DeleteChunks(chunklist);
					FileChunkMappings.deleteFile(fileId);
					((DefaultTableModel) table.getModel())
							.removeRow(selectedRow);
					messagebox.showMessageDialog(frame, "Remove successfully!");
				}
			}
		});
		panelButton.add(btnRemove);

		btnMove = CreateButton(" Move", "images/btn_move.png");
		panelButton.add(btnMove);

		JLabel label = new JLabel("");
		label.setOpaque(true); // Set it true to color the label
		label.setBackground(Color.WHITE);
		panelButton.add(label);

		JLabel label1 = new JLabel("");
		label1.setOpaque(true);
		label1.setBackground(Color.WHITE);
		panelButton.add(label1);

	}

	private JButton CreateButton(String text, String iconPath) {
		JButton btn = new JButton(text, new ImageIcon(iconPath));
		btn.setBackground(Color.WHITE);
		btn.setFocusable(false);
		btn.setBorderPainted(false);
		return btn;
	}

	private JTable CreateTable() {

		DefaultTableModel tableModel = new DefaultTableModel() {

			@Override
			public boolean isCellEditable(int row, int column) {
				// all cells false
				return false;
			}
		};

		tableModel.addColumn("File ID");
		tableModel.addColumn("File Name");
		tableModel.addColumn("File Size");
		tableModel.addColumn("Upload Time");

		JTable table = new JTable(tableModel);
		table.setShowVerticalLines(false);

		return table;
	}

	public void initTableData() {
		FileChunkMappings m = new FileChunkMappings();
		ArrayList<FileProfile> fileList = null;

		fileList = m.readFileList();

		for (FileProfile file : fileList) {
			addRowToTable(file.getId(), file.getName(), Long.toString(file
					.getSize()), file.getUploadDate().toString());
		}
	}

	public void addRowToTable(String id, String fileName, String size,
			String uploadTime) {
		Object[] row = { id, fileName, size + " K", uploadTime };
		((DefaultTableModel) table.getModel()).addRow(row);
	}
}
