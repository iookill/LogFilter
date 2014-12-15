/*
RecentFileMenu.java - menu to store and display recently used files.
 
 Copyright  (C) 2005 Hugues Johnson
 
This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.
 
This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
the GNU General Public License for more details.
 
You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 */

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.LineNumberReader;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

/**
 * A menu used to store and display recently used files.
 * Saves entries in a file called "[user.dir]/[name passed to constructor].recent".
 * @author Hugues Johnson
 */
public abstract class RecentFileMenu extends JMenu{
	private String pathToSavedFile; //where to save the items in this menu
	private int itemCount; //how many items in the menu
	private String[] recentEntries; //the recent file entries
	private final static String defaultText="__R_e_C_e_N_t__:_?"; //colon & question mark are not allowed as a file name in any OS that I'm aware of
	
	/**
	 * Create a new instance of RecentFileMenu.
	 * @param name The name of this menu, not displayed but used to store the list of recently used file names.
	 * @param count The number of recent files to store.
	 */
	public RecentFileMenu(String name,int count){
		super();
		this.setText("Recent");
		this.setMnemonic('R');
		this.itemCount=count;
		//initialize default entries
		this.recentEntries=new String[count];
		for(int index=0;index<this.itemCount;index++){
			this.recentEntries[index]=defaultText;
		}
		//figure out the name of the recent file
		this.pathToSavedFile=System.getProperty("user.dir");
		if((this.pathToSavedFile==null)||(this.pathToSavedFile.length()<=0)){
			this.pathToSavedFile=new String(name+".ini"); //probably unreachable
		} else if(this.pathToSavedFile.endsWith(File.separator)){
			this.pathToSavedFile=this.pathToSavedFile+name+".ini";
		} else{
			this.pathToSavedFile=this.pathToSavedFile+File.separator+name+".ini";
		}
		//load the recent entries if they exist
		File recentFile=new File(this.pathToSavedFile);
		if(recentFile.exists()){
			try{
				LineNumberReader reader=new LineNumberReader(new FileReader(this.pathToSavedFile));
				while(reader.ready()){
					this.addEntry(reader.readLine(),false);
				}
			} catch(Exception x){
				x.printStackTrace();
			}		
		} else{ //disable
			this.setEnabled(false);
		}
	}
	
	/**
	 * Adds a new entry to the menu. Moves everything "down" one row.
	 * @param filePath The new path to add.
	 */
	public void addEntry(String filePath){
		this.addEntry(filePath,true);
	}

	/**
	 * Adds a new entry to the menu. Moves everything "down" one row.
	 * @param filePath The new path to add.
	 * @param updateFile Whether to update the saved file, only false when called from constructor.
	 */
	private void addEntry(String filePath,boolean updateFile){
		//check if this is disabled 
		if(!this.isEnabled()){
			this.setEnabled(true);
		}
		//clear the existing items
		this.removeAll();
		//move everything down one slot
		int count=this.itemCount-1;
		for(int index=count;index>0;index--){
			//check for duplicate entry
			if(!this.recentEntries[index-1].equalsIgnoreCase(filePath)){
				this.recentEntries[index]=new String(this.recentEntries[index-1]);
			}
		}
		//add the new item, check if it's not alredy the first item
		if(!this.recentEntries[0].equalsIgnoreCase(filePath)){
			this.recentEntries[0]=new String(filePath);
		}
		//add items back to the menu
		for(int index=0;index<this.itemCount;index++){
        	JMenuItem menuItem=new JMenuItem();
			menuItem.setText(this.recentEntries[index]);
			if(this.recentEntries[index].equals(defaultText)){
				menuItem.setVisible(false);
			} else{
				menuItem.setVisible(true);
				menuItem.setToolTipText(this.recentEntries[index]);
				menuItem.setActionCommand(this.recentEntries[index]);
				menuItem.addActionListener(new ActionListener(){
		            public void actionPerformed(ActionEvent actionEvent){
		                onSelectFile(actionEvent.getActionCommand());
		            }
		        });
	        }
        	this.add(menuItem);
        }
        //update the file
		if(updateFile){
			try{
				FileWriter writer=new FileWriter(new File(this.pathToSavedFile));
				int topIndex=this.itemCount-1;
				for(int index=topIndex;index>=0;index--){
					if(!this.recentEntries[index].equals(defaultText)){
						writer.write(this.recentEntries[index]);
						writer.write("\n");
					}
				}
				writer.flush();
				writer.close();
			} catch(Exception x){
				x.printStackTrace();
			}
		}
	}
		
	/**
	 * Event that fires when a recent file is selected from the menu. Override this when implementing.
	 * @param filePath The file that was selected.
	 */
	public abstract void onSelectFile(String filePath);
}