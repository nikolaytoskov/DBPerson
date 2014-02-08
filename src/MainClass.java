import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class MainClass extends JFrame {

	/**
	 * @param args
	 */
	Connection conn;
	Statement state = null;
	PreparedStatement prepState = null;
	ResultSet result = null;
	
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
	String[] searchCrit = {"First Name","Last Name","Age","Mail","Gender"};
	JComboBox searchCombo =new JComboBox(searchCrit);
	
	JButton insertB = new JButton("Add Person");
	JButton searchB = new JButton("Search");
	
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
		
		insertB.addActionListener(new InsertAction());
		searchB.addActionListener(new SearchAction());
		//downPanel
	}// end init()
	
	class SearchAction implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			if(searchCombo.getSelectedIndex() == 0){
				conn = DBUtil.getConnected();
				try {
					prepState =conn.prepareStatement("select top 1 * from person where f_name=?");
					prepState.setString(1, fNameField.getText().trim());
					result = prepState.executeQuery();
					
					while(result.next()){
						String mail = result.getString("mail");
						String lN = result.getString("l_name");
						maileField.setText(mail);
						lNameField.setText(lN);
					}// end while
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}// end if clause
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
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}// end actionPerformed
		
	}// end InsertAction
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MainClass jFrame = new MainClass();
		jFrame.setVisible(true);
		jFrame.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

}
