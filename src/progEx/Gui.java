package progEx;

import java.awt.EventQueue;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import javax.swing.text.Document;

import com.mysql.cj.protocol.x.SyncFlushDeflaterOutputStream;

import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ResourceBundle.Control;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Gui {

	private Connector connector;
	private JFrame frame;
	private JTextField textFieldId;
	private JTextField textFieldBrand;
	private JTextField textFieldType;
	private JTextField textFieldPrice;
	private JTable table;
	private JScrollPane scrollPane;
	private JButton btnDeleteBike;
	private boolean tablePressed = false;
	private int initialId;
	private String initialBrand;
	private String initialType;
	private String initialPrice;
	private int checkId;
	private boolean changed;
	private JTextField textFieldSearch;
	private boolean canSelect = true;
	private JButton btnSearch;
	private boolean searchButtonPressed = false;

	/**
	 * Create the application.
	 */
	public Gui() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		connector = new Connector();
		frame = new JFrame();
		frame.getContentPane().setBackground(new Color(51, 102, 0));
		frame.setBackground(new Color(255, 255, 255));
		frame.setSize(800, 600);
		frame.setResizable(false);
//		frame.setBounds(100, 100, 800, 393);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		JButton btnUpdateBike = new JButton("Update Bike");
		btnUpdateBike.setEnabled(false); // initialize update button earlier because I need it in table
											// mouseclicklistener
											// to disable button if no tablerowis selected

		JPanel panel = new JPanel();
		// panel mouseclickevent to deselect table when pressed somewhere else
		panel.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				// if the table was pressed befere
				// than clear selection and textfields
				if (tablePressed == true) {
					btnUpdateBike.setEnabled(false);
					table.clearSelection();
					textFieldId.setText("");
					textFieldBrand.setText("");
					textFieldType.setText("");
					textFieldPrice.setText("");
					tablePressed = false; // set tablepressed to false again
				}

			}
		});
		panel.setBackground(new Color(153, 204, 153));
		panel.setBounds(12, 12, 776, 539);
		frame.getContentPane().add(panel);
		panel.setLayout(null);

		/*
		 * Labels
		 */
		JLabel articleIdLabel = new JLabel("Article id:");
		articleIdLabel.setBounds(54, 65, 90, 34);
		articleIdLabel.setFont(articleIdLabel.getFont().deriveFont(15.0f));
		panel.add(articleIdLabel);

		JLabel articleBrandLabel = new JLabel("Article Brand:");
		articleBrandLabel.setFont(articleBrandLabel.getFont().deriveFont(15.0f));
		articleBrandLabel.setBounds(54, 135, 118, 34);
		panel.add(articleBrandLabel);

		JLabel articlePriceLabel = new JLabel("Article Type:");
		articlePriceLabel.setFont(articlePriceLabel.getFont().deriveFont(15.0f));
		articlePriceLabel.setBounds(54, 205, 118, 34);
		panel.add(articlePriceLabel);

		JLabel articleTypeLabel = new JLabel("Article Price:");
		articleTypeLabel.setFont(articleTypeLabel.getFont().deriveFont(15.0f));
		articleTypeLabel.setBounds(54, 275, 118, 34);
		panel.add(articleTypeLabel);

		/*
		 * TextFields
		 */
		Font textFieldFont = new Font("SansSerif", Font.BOLD, 15);
		textFieldId = new JTextField();
		textFieldId.setFont(textFieldFont);
		textFieldId.setBounds(172, 72, 181, 20);
		panel.add(textFieldId);
		textFieldId.setColumns(10);

		textFieldBrand = new JTextField();
		textFieldBrand.setFont(new Font("SansSerif", Font.BOLD, 15));
		textFieldBrand.setColumns(10);
		textFieldBrand.setBounds(172, 143, 181, 20);
		panel.add(textFieldBrand);

		textFieldType = new JTextField();
		textFieldType.setFont(new Font("SansSerif", Font.BOLD, 15));
		textFieldType.setColumns(10);
		textFieldType.setBounds(172, 213, 181, 20);
		panel.add(textFieldType);

		textFieldPrice = new JTextField();
		textFieldPrice.setFont(new Font("SansSerif", Font.BOLD, 15));
		textFieldPrice.setColumns(10);
		textFieldPrice.setBounds(172, 283, 181, 20);
		panel.add(textFieldPrice);

		scrollPane = new JScrollPane();
		scrollPane.setBounds(365, 74, 399, 397);
		panel.add(scrollPane);

		table = new JTable() {
			private static final long serialVersionUID = 1L;

			public boolean isCellEditable(int row, int column) { // to make cells uneditable
				return false;

			};

		};

		textFieldSearch = new JTextField();
		textFieldSearch.setFont(new Font("SansSerif", Font.BOLD, 15));
		textFieldSearch.setColumns(10);
		textFieldSearch.setBounds(499, 42, 265, 20);
		panel.add(textFieldSearch);

		/*
		 * table
		 */

		table.setAutoCreateRowSorter(false);
		table.setBackground(new Color(102, 204, 153));
		table.setFont(new Font("SansSerif", Font.BOLD, 15));

		// columns for the headlines of the rows
		Object[] column = { "article id", "brand", "type", "price" };

		// 4 rows for ID, Brand, Price and Type
		String[] row = new String[4];

		// set table model
		DefaultTableModel model = new DefaultTableModel(column, 0);

		// set the tablemodel to the created model
		connector.setTable(model);

		table.setModel(model);
		scrollPane.setViewportView(table);

		/*
		 * mouslistener on table to listen on a row click and fill the textfields with
		 * the data from the clicked row
		 */
		table.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				// save selected row from table to an variable
				int i = table.getSelectedRow();
				// if a row is selected
				// than set the textfields to the rows values
				if (canSelect) {
					tablePressed = true;
					btnUpdateBike.setEnabled(true);
					// fill the textFields with selected data
					textFieldId.setText(model.getValueAt(i, 0).toString());
					initialId = Integer.parseInt(textFieldId.getText());
					textFieldBrand.setText(model.getValueAt(i, 1).toString());
					initialBrand = textFieldBrand.getText();
					textFieldType.setText(model.getValueAt(i, 2).toString());
					initialType = textFieldType.getText();
					textFieldPrice.setText(model.getValueAt(i, 3).toString());
					initialPrice = textFieldPrice.getText();
				}
			}
		});

		// Buttons
		// addbike button
		JButton btnAddBike = new JButton("Add Bike");
		// button is not enabled
		btnAddBike.setEnabled(false);

		// add button actionlistener
		btnAddBike.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				try {
					Float.parseFloat(textFieldPrice.getText()); // exception if input is not a float

					boolean idExists = false;
					// add the input text into the rows
					int id = Integer.parseInt(textFieldId.getText());

					// iterate though rows to check for input id
					for (int i = 0; i < model.getRowCount(); i++) {
						// if the ID at Row i equals user input ID
						// than give error, because not duplicates allowed
						if (model.getValueAt(i, 0).equals(textFieldId.getText())) {
							idExists = true;
						}
					}
					if (idExists == false) {
						row[0] = textFieldId.getText();
						row[1] = textFieldBrand.getText();
						row[2] = textFieldType.getText();
						row[3] = textFieldPrice.getText();

						// add the row to the model
						model.addRow(row);
						// add the row to the database
						connector.add(id, row[1], row[2], row[3]);

						// empty the textfields
						textFieldId.setText("");
						textFieldBrand.setText("");
						textFieldType.setText("");
						textFieldPrice.setText("");

						// succes messagebox
						JOptionPane.showMessageDialog(null, "Saved succesfully", "Saved", 1);
					} else {
						JOptionPane.showMessageDialog(null, "This id already exists!", "Error", 0);
					}

				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, "id nust be integer and price must be a number!", "wrong input",
							0);

				}

			}
		});
		btnAddBike.setBounds(12, 331, 117, 25);
		panel.add(btnAddBike);

		// updatebike button
		// disable button while textfields are empty ( check document listener)
		btnUpdateBike.setEnabled(false);

		// update button actionListener
		btnUpdateBike.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				/*
				 * # try catch to prevent input non numerical input for id and price
				 */
				try {
					String input[] = { textFieldBrand.getText(), textFieldType.getText(), textFieldPrice.getText() };
					Float.parseFloat(textFieldPrice.getText()); // exception if input is not a float
					Integer.parseInt(textFieldId.getText()); // exceptoin if input is not integer
					boolean idExists = false;
					// iterate though rows to check for input id
					for (int i = 0; i < model.getRowCount(); i++) {
						// if the ID at Row i equals user input ID
						// than give error, because not duplicates allowed
						if (model.getValueAt(i, 0).equals(textFieldId.getText())) {
							idExists = true;
						}
					}

					// if id is identical but input(s) are different
					if ((initialId == Integer.parseInt(textFieldId.getText()))
							&& (initialBrand != textFieldBrand.getText() || initialType != textFieldType.getText()
									|| initialPrice != textFieldPrice.getText())) {
						/*
						 * this is for updating the table by clicking on the row
						 */
						int i = table.getSelectedRow();
						System.out.println(initialId);
						System.out.println(textFieldId.getText());
						// safe the textfield inputs in a string

						/*
						 * following lines are for updating the table by typing in the textfields
						 * manually
						 */

						// update the Row
						// if the ID is not in the row
						if (updateRow(textFieldId.getText(), input, model) == false) {
							JOptionPane.showMessageDialog(null, "no Element with such ID", "Error", 0);
						}

						else {
							// update the database to the changes
							connector.updateDatabase(Integer.parseInt(textFieldId.getText()), initialId,
									textFieldBrand.getText(), textFieldType.getText(), textFieldPrice.getText());
							System.out.println("first if");
							changed = updateRow(textFieldId.getText(), input, model);
							// success message
							JOptionPane.showMessageDialog(null, "Update done", "Update", 1);
						}

						// clear the textfields
						textFieldId.setText("");
						textFieldBrand.setText("");
						textFieldType.setText("");
						textFieldPrice.setText("");

					}

					// if ids are differnet but input same, change id
					else if ((initialId != Integer.parseInt(textFieldId.getText()))) {
						if (idExists) {
							JOptionPane.showMessageDialog(null, "This id already exists!", "Error", 0);
						} else {
							String inputLocal[] = { textFieldId.getText(), textFieldBrand.getText(),
									textFieldType.getText(), textFieldPrice.getText() };

							connector.updateDatabase(Integer.parseInt(textFieldId.getText()), initialId,
									textFieldBrand.getText(), textFieldType.getText(), textFieldPrice.getText());
							// success message
							JOptionPane.showMessageDialog(null, "Update done", "Update", 1);
							updateRowWithId(initialId, textFieldId.getText(), input, model);
							System.out.println("second if");
						}
						// clear the textfields
						textFieldId.setText("");
						textFieldBrand.setText("");
						textFieldType.setText("");
						textFieldPrice.setText("");
					}

					else {
						JOptionPane.showMessageDialog(null, "no such ID", "Error", 0);
					}
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, "id must be an integer and price must be a number!",
							"wrong input", 0);
				}

			}
		});
		btnUpdateBike.setBounds(220, 331, 133, 25);
		panel.add(btnUpdateBike);

		// deletebike button
		btnDeleteBike = new JButton("Delete Bike");
		btnDeleteBike.setBounds(220, 386, 133, 25);
		panel.add(btnDeleteBike);
		btnDeleteBike.setEnabled(false);

		JButton btnClear = new JButton("Clear");
		btnClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				textFieldSearch.setText("");
				textFieldId.setText("");
				textFieldBrand.setText("");
				textFieldType.setText("");
				textFieldPrice.setText("");
				table.clearSelection();

			}
		});
		btnClear.setBounds(12, 386, 117, 25);
		panel.add(btnClear);

		btnSearch = new JButton("Search");
		btnSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				DefaultTableModel tableForSearching = (DefaultTableModel) table.getModel();
				String search = textFieldSearch.getText().toLowerCase();
				TableRowSorter<DefaultTableModel> tableRowSorter = new TableRowSorter<DefaultTableModel>(
						tableForSearching);
				table.setRowSorter(tableRowSorter);
				tableRowSorter.setRowFilter(RowFilter.regexFilter(search, 0));
				if (textFieldSearch.getText().equals("") == false) {
					searchButtonPressed = true;
					canSelect = false;
					table.setFocusable(false);
					table.setRowSelectionAllowed(false);
					for (int row = 0; row < table.getRowCount(); row++) {
//			                System.out.println(table.getModel().getValueAt(table.convertRowIndexToModel(row), 0));
						textFieldId
								.setText(table.getModel().getValueAt(table.convertRowIndexToModel(row), 0).toString());
						initialId = Integer.parseInt(textFieldId.getText());
						textFieldBrand
								.setText(table.getModel().getValueAt(table.convertRowIndexToModel(row), 1).toString());
						initialBrand = textFieldBrand.getText();
						textFieldType
								.setText(table.getModel().getValueAt(table.convertRowIndexToModel(row), 2).toString());
						initialType = textFieldType.getText();
						textFieldPrice
								.setText(table.getModel().getValueAt(table.convertRowIndexToModel(row), 3).toString());
						initialPrice = textFieldPrice.getText();

					}
					table.setAutoCreateRowSorter(false);
				} else {
					tableRowSorter.setSortable(0, false);
					tableRowSorter.setSortable(1, false);
					tableRowSorter.setSortable(2, false);
					tableRowSorter.setSortable(3, false);

					canSelect = true;
					table.setFocusable(true);
					table.setRowSelectionAllowed(true);
					btnUpdateBike.setEnabled(false);
					searchButtonPressed = false;
					table.setModel(model);
					table.setAutoCreateRowSorter(false);
				}
			}
		});
		btnSearch.setBounds(370, 37, 117, 25);
		panel.add(btnSearch);

		btnDeleteBike.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int pressed = -1;
				pressed = Integer.parseInt(textFieldId.getText());
				boolean validId = false;
				int y = table.getSelectedRow();

				// if something is pressed
				if (pressed >= 0) {
					// only if y isn't empty
					// so if a row was selected before
					if (y >= 0) {
						// set values of the table to textfieldValues
						// arg1 = value, arg2 = row, arg3 = column
						model.removeRow(y);
						connector.deleleteFromDatabase(pressed); // delete row from database
						validId = true;
						// deselect the selected row
						table.clearSelection();
					}
					// this is for checking if the input was manually made
					if (pressed >= 0) {
						// iterate though rows to check for input id
						for (int i = 0; i < model.getRowCount(); i++) {
							// if the ID at Row i equals user input ID
							// than delete the row in table and database
							if (model.getValueAt(i, 0).equals(textFieldId.getText())) {
								model.removeRow(i);
								connector.deleleteFromDatabase(pressed);
								JOptionPane.showMessageDialog(null, "Bike deleted", "Delete", 1);
								validId = true; // valid id to set clear that a row was found and deleted
							}
						}
					}
					// if no row was found error message
					if (validId == false) {
						JOptionPane.showMessageDialog(null, "No bike with such ID", "Error", 0);
					}
				}

				// clear textfields
				textFieldId.setText("");
				textFieldBrand.setText("");
				textFieldType.setText("");
				textFieldPrice.setText("");

			}
		});

		/*
		 * the following is for handling the buttons when textfields are empty
		 */
		// create document listener
		// to listen to more documents
		DocumentListener doc = new DocumentListener() {

			// removeUpdate AFAIK function called when textfield
			// is being emptied or deleted
			public void removeUpdate(DocumentEvent arg0) {
				// check if remaining textfield is empty
				// if so, diasble buttons
				if (textFieldId.getText().equals("") || textFieldBrand.getText().equals("")
						|| textFieldType.getText().equals("") || textFieldPrice.getText().equals("")) {
					btnAddBike.setEnabled(false);
					btnUpdateBike.setEnabled(false);
					btnDeleteBike.setEnabled(false);
				}
				if (searchButtonPressed)
					btnUpdateBike.setEnabled(true);
			}

			// insertupdate function AFAIK called when textfield
			// is being updated
			public void insertUpdate(DocumentEvent arg0) {
				// check if any textfiels is empty
				// if so disable the buttons
				// else enable them
				if (textFieldId.getText().equals("") || textFieldBrand.getText().equals("")
						|| textFieldType.getText().equals("") || textFieldPrice.getText().equals("")) {
					btnAddBike.setEnabled(false);
					btnUpdateBike.setEnabled(false);
					if (textFieldId.getText().equals("") == false)
						btnDeleteBike.setEnabled(true);
				} else {
					btnAddBike.setEnabled(true);

					if (table.getSelectedRow() >= 0)
						btnUpdateBike.setEnabled(true);

					btnDeleteBike.setEnabled(true);
				}
				if (searchButtonPressed)
					btnUpdateBike.setEnabled(true);
			}

			// changeupdate function called when
			// a textfield is being changed
			public void changedUpdate(DocumentEvent arg0) {
				// check if any textfield AFAIK is changed
				// if so disable the buttons
				// else enable them
				if (textFieldId.getText().equals("") || textFieldBrand.getText().equals("")
						|| textFieldType.getText().equals("") || textFieldPrice.getText().equals("")) {
					btnAddBike.setEnabled(false);
//					btnUpdateBike.setEnabled(false);
					if (textFieldId.getText().equals("") == false)
						btnDeleteBike.setEnabled(true);
				} else {
					btnAddBike.setEnabled(true);
//					btnUpdateBike.setEnabled(true);
					btnDeleteBike.setEnabled(true);
				}
				if (searchButtonPressed)
					btnUpdateBike.setEnabled(true);
			}
		};

		// add documents from the textfields to the documentlistener
		// to acces them all at the same time
		textFieldId.getDocument().addDocumentListener(doc);
		textFieldBrand.getDocument().addDocumentListener(doc);
		textFieldType.getDocument().addDocumentListener(doc);
		textFieldPrice.getDocument().addDocumentListener(doc);

		// set the frame visible
		frame.setVisible(true);
	}

	/*
	 * function for updating a row arg1 = id arg2 = new input to change the row arg3
	 * = the table model to change the row in the model
	 * 
	 * @return = boolean if such an article is in the database or not
	 */
	public boolean updateRow(String article_id, String[] input, DefaultTableModel model) {
		boolean check = false;
		// iterate through rows
		for (int i = 0; i < model.getRowCount(); i++) {
			// for each row, check if the id which the user is searching for exists
			if (model.getValueAt(i, 0).equals(article_id)) {
				// if exist check = true
				check = true;
				// fill row with updated data
				for (int j = 1; j < input.length + 1; j++) {
					model.setValueAt(input[j - 1], i, j);
				}
			}
		}
		return check;
	}

	public void updateRowWithId(int initalId, String new_id, String[] input, DefaultTableModel model) {
		// iterate thourgh the rows
		for (int i = 0; i < model.getRowCount(); i++) {
			if (model.getValueAt(i, 0).equals(Integer.toString(initalId))) {
				System.out.println("im in");
				// for each row, check if the id which the user is searching for exists
				model.setValueAt(new_id, i, 0);
				model.setValueAt(input[0], i, 1);
				model.setValueAt(input[1], i, 2);
				model.setValueAt(input[2], i, 3);
			}
		}
	}
}
