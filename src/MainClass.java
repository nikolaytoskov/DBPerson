import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.table.JTableHeader;


public class MainClass extends JFrame {

	/**
     * 
     */
    private static final long serialVersionUID = 1L;

    	boolean flag = false;
    	int id = 0;
	Connection conn;
	Statement state = null;
	PreparedStatement prepState = null;
	ResultSet result = null;
	JTable table = new JTable();
	MyModel model;
	
	JPanel topPanel = new JPanel();
	JPanel midPanel = new JPanel();
	JPanel downPanel = new JPanel();
	
	JLabel fNameL = new JLabel("First Name");
	JLabel lNameL = new JLabel("Last Name");
	JLabel ageL = new JLabel("Age");
	JLabel mailL = new JLabel("Mail");
	JLabel genderL = new JLabel("Gender");
	
	JTextField fNameField = new JTextField(20);
	JTextField lNameField = new JTextField(20);
	JTextField maileField = new JTextField(20);
	JTextField ageField = new JTextField(20);
	
	String[] comboContent = {"female","male"};
	JComboBox genderCombo =new JComboBox(comboContent);
	String[] searchCrit = {"First Name","Last Name","Age","Mail","Gender", "Person_ID"};
	JComboBox searchCombo =new JComboBox(searchCrit);
	
	JButton insertB = new JButton("Add Person");
	JButton searchB = new JButton("Search");
	JButton refreshB = new JButton("Show All");
	JButton editB = new JButton("Edit");
	JButton startB = new JButton("Start");
	
	public MainClass(){
		super();
		init();
	}
	
	public void init(){
		this.setLayout(new GridLayout(3,1));
		this.setSize(600, 400);
		this.add(topPanel);
		this.add(midPanel);
		this.add(downPanel);
		
		//topPanel
		topPanel.setLayout(new GridLayout(5,2));
		topPanel.add(fNameL);
		topPanel.setBackground(Color.CYAN);
		topPanel.add(fNameField);
		topPanel.add(lNameL);
		topPanel.add(lNameField);
		topPanel.add(ageL);
		topPanel.add(ageField);
		topPanel.add(mailL);
		topPanel.add(maileField);
		topPanel.add(genderL);
		topPanel.add(genderCombo);
		
		//midPanel
		midPanel.setLayout(new FlowLayout());
		midPanel.add(insertB);
		midPanel.add(searchCombo);
		midPanel.add(searchB);
		midPanel.add(refreshB);
		midPanel.add(editB);
		midPanel.add(startB);
		midPanel.setBackground(Color.darkGray);
		
		insertB.addActionListener(new InsertAction());
		searchB.addActionListener(new SearchAction());
		refreshB.addActionListener(new ShowAllAction());
		editB.addActionListener(new EditAction());
		startB.addActionListener(new StartAction());
		
		//downPanel
		JTableHeader header = table.getTableHeader();
		header.setBackground(Color.red);
		downPanel.setBackground(Color.green);
		downPanel.setLayout(new FlowLayout());
		JScrollPane tableScroll = new JScrollPane(table);
		tableScroll.setPreferredSize(new Dimension(450, 100));
		downPanel.add(tableScroll);//slagame skrolira6t panel i wytre slagame tablicata
		try {
		    table.setModel(getModel());
		} catch (Exception e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}
	}// end init()
	
	class StartAction implements ActionListener{

	    @Override
	    public void actionPerformed(ActionEvent arg0) {
		flag = !flag;
		final ArrayList<String> temp = getPNames();
		final Random randNum = new Random();
		new Thread(){  //syzdavane na nishka za startirane na wytre6na programa. Izpalnqvane na neshto mnogokratno
		    public void run(){
			while(flag){
			    fNameField.setText(temp.get(randNum.nextInt(temp.size())));
			    try {
				sleep(100); //zabavqme izpisvaneto sys 100 ms
			    } catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			    }
			}
		    }
		}.start();
		
	    }
	    
	}
	
	public ArrayList<String> getPNames(){ //sazdavame metod za jurkane na imenata i izbor, zatowa syzdawame list
	    conn = DBUtil.getConnected();
	    ArrayList<String> names = new ArrayList<String>();
	    try {
		prepState = conn.prepareStatement("Select f_name, l_name from person");
		result = prepState.executeQuery();
		
		while (result.next()){
		    names.add(result.getString("f_name")+" "+result.getString("l_name"));
		}
		return names;
	    } catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	    return names;
	}
	
	class EditAction implements ActionListener{

	    @Override
	    public void actionPerformed(ActionEvent arg0) {
		conn = DBUtil.getConnected();
		try {
		    prepState = conn.prepareStatement("update person set f_name = ?, l_name=?, mail=? where person_id = ?");
		    prepState.setString(1, fNameField.getText());
		    prepState.setInt(2, id);
		    prepState.executeUpdate();
		    refreshContent();
		} catch (SQLException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}
	    }
	    
	}
	
	class ShowAllAction implements ActionListener{
	    public void actionPerformed(ActionEvent e){
		refreshContent();
	    }
	}
	
	class SearchAction implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			if(searchCombo.getSelectedIndex() == 0){
				conn = DBUtil.getConnected();
				try {
					prepState =conn.prepareStatement("select * from person where f_name=?");
					prepState.setString(1, fNameField.getText().trim());
					result = prepState.executeQuery();
					
					try {
					    model = new MyModel(result);
					    model.fireTableDataChanged();
					    table.setModel(model);
					    table.repaint();
					} catch (Exception e1) {
					    // TODO Auto-generated catch block
					    e1.printStackTrace();
					}
					
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}// end if clause
			if(searchCombo.getSelectedIndex() == searchCrit.length-1){
			    conn = DBUtil.getConnected();
			    try {
				prepState = conn.prepareStatement("select* from person where person_id=?");
				prepState.setInt(1, Integer.parseInt(fNameField.getText().trim()));
				result = prepState.executeQuery();
				id = Integer.parseInt(fNameField.getText().trim());
				
				while(result.next()){
				    fNameField.setText(result.getString("f_name"));
				    lNameField.setText(result.getString("l_name"));
				    maileField.setText(result.getString("MAIL")); 
				}
			    } catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			    }
			    
			}
		}// end actionPerformed
		
	}// end SearchAction
	
	class InsertAction implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			String fN = fNameField.getText().trim();
			String lN = lNameField.getText().trim();
			String mail = maileField.getText().trim();
			int a = Integer.parseInt(ageField.getText());
			boolean gender;
			if(genderCombo.getSelectedIndex() == 0){
				gender = false;
			}// end if clause
			else{
				gender = true;
			}// end else clause
			
			try {
				conn = DBUtil.getConnected();
				prepState = conn.prepareStatement("insert into person values (null,?,?,?,?,?)");
				prepState.setString(1, fN);
				prepState.setString(2, lN);
				prepState.setBoolean(3, gender);
				prepState.setInt(4, a);
				prepState.setString(5, mail);
				prepState.execute();
				System.out.println("Success");
				prepState.close();
				conn.close();
				refreshContent();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}// end actionPerformed
		
	}// end InsertAction
	
	public MyModel getModel() throws Exception{
	    state = DBUtil.getConnected().createStatement();
	    result = state.executeQuery("select* from person ");
	    model = new MyModel (result);
	    return model;
	}
	
	public void refreshContent(){
	    try{
		model = getModel();
		model.fireTableDataChanged();
		table.setModel(model);
		table.repaint();
	    }
	    catch (Exception ex){
		
	    }
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MainClass jFrame = new MainClass();
		jFrame.setVisible(true);
		jFrame.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

}
