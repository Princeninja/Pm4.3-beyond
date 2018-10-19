package palmed;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import palmed.pmUser.Role;

import usrlib.XMLElement;
import usrlib.XMLParseException;

public class ERxConfig {
	
	private final static String fn_config = "erxconfig.xml";
	private final static String fn_config_demo = "erxconfig-demo.xml";

	private String partnerName = "";
	private String partnerUsername = "";
	private String partnerPassword = "";
	private String partnerProduct = "";
	private String partnerVersion = "";
	
	private String clickThru = "";
	private String webServices = "";
	
	
	private String accountName = "";
	private String accountID = "";
	private String siteID = "";
	private String accountStreet = "";
	private String accountLine2 = "";
	private String accountState = "";
	private String accountCity = "";
	private String accountZip = "";
	private String accountCountry = "";
	private String accountPhone = "";
	private String accountFax = "";
	private String accountEmail = "";
	
	
	private String locationName = "";
	private String locationID = "";
	private String locationStreet = "";
	private String locationLine2 = "";
	private String locationCity = "";
	private String locationState = "";
	private String locationZip = "";
	private String locationCountry = "";
	private String locationPhone = "";
	private String locationFax = "";
	private String locationEmail = "";
	private String locationContact = "";
	



	private boolean pollStatus = false;
	private boolean sendMeds = false;
	private boolean sendPARs = false;
	private boolean sendProbs = false;
	private boolean newcropManagedMeds = false;
	private boolean newcropManagedPARs = false;
	
		
	
	
	
	
	/**
	 * @return the partnerName
	 */
	public String getPartnerName() {
		return partnerName;
	}
	/**
	 * @param partnerName the partnerName to set
	 */
	public void setPartnerName(String partnerName) {
		this.partnerName = partnerName;
	}
	/**
	 * @return the partnerUsername
	 */
	public String getPartnerUsername() {
		return partnerUsername;
	}
	/**
	 * @param partnerUsername the partnerUsername to set
	 */
	public void setPartnerUsername(String partnerUsername) {
		this.partnerUsername = partnerUsername;
	}
	/**
	 * @return the partnerPassword
	 */
	public String getPartnerPassword() {
		return partnerPassword;
	}
	/**
	 * @param partnerPassword the partnerPassword to set
	 */
	public void setPartnerPassword(String partnerPassword) {
		this.partnerPassword = partnerPassword;
	}
	/**
	 * @return the partnerProduct
	 */
	public String getPartnerProduct() {
		return partnerProduct;
	}
	/**
	 * @param partnerProduct the partnerProduct to set
	 */
	public void setPartnerProduct(String partnerProduct) {
		this.partnerProduct = partnerProduct;
	}
	/**
	 * @return the partnerVersion
	 */
	public String getPartnerVersion() {
		return partnerVersion;
	}
	/**
	 * @param partnerVersion the partnerVersion to set
	 */
	public void setPartnerVersion(String partnerVersion) {
		this.partnerVersion = partnerVersion;
	}
	/**
	 * @return the clickThru
	 */
	public String getClickThru() {
		return clickThru;
	}
	/**
	 * @param clickThru the clickThru to set
	 */
	public void setClickThru(String clickThru) {
		this.clickThru = clickThru;
	}
	/**
	 * @return the webServices
	 */
	public String getWebServices() {
		return webServices;
	}
	/**
	 * @param webServices the webServices to set
	 */
	public void setWebServices(String webServices) {
		this.webServices = webServices;
	}
	/**
	 * @return the accountName
	 */
	public String getAccountName() {
		return accountName;
	}
	/**
	 * @param accountName the accountName to set
	 */
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	/**
	 * @return the accountID
	 */
	public String getAccountID() {
		return accountID;
	}
	/**
	 * @param accountID the accountID to set
	 */
	public void setAccountID(String accountID) {
		this.accountID = accountID;
	}
	/**
	 * @return the siteID
	 */
	public String getSiteID() {
		return siteID;
	}
	/**
	 * @param siteID the siteID to set
	 */
	public void setSiteID(String siteID) {
		this.siteID = siteID;
	}
	/**
	 * @return the accountStreet
	 */
	public String getAccountStreet() {
		return accountStreet;
	}
	/**
	 * @param accountStreet the accountStreet to set
	 */
	public void setAccountStreet(String accountStreet) {
		this.accountStreet = accountStreet;
	}
	/**
	 * @return the accountLine2
	 */
	public String getAccountLine2() {
		return accountLine2;
	}
	/**
	 * @param accountLine2 the accountLine2 to set
	 */
	public void setAccountLine2(String accountLine2) {
		this.accountLine2 = accountLine2;
	}
	/**
	 * @return the accountCity
	 */
	public String getAccountCity() {
		return accountCity;
	}
	/**
	 * @param accountCity the accountCity to set
	 */
	public void setAccountCity(String accountCity) {
		this.accountCity = accountCity;
	}
	/**
	 * @return the accountState
	 */
	public String getAccountState() {
		return accountState;
	}
	/**
	 * @param accountState the accountState to set
	 */
	public void setAccountState(String accountState) {
		this.accountState = accountState;
	}
	/**
	 * @return the accountZip
	 */
	public String getAccountZip() {
		return accountZip;
	}
	/**
	 * @param accountZip the accountZip to set
	 */
	public void setAccountZip(String accountZip) {
		this.accountZip = accountZip;
	}
	/**
	 * @return the accountCountry
	 */
	public String getAccountCountry() {
		return accountCountry;
	}
	/**
	 * @param accountCountry the accountCountry to set
	 */
	public void setAccountCountry(String accountCountry) {
		this.accountCountry = accountCountry;
	}
	/**
	 * @return the accountPhone
	 */
	public String getAccountPhone() {
		return accountPhone;
	}
	/**
	 * @param accountPhone the accountPhone to set
	 */
	public void setAccountPhone(String accountPhone) {
		this.accountPhone = accountPhone;
	}
	/**
	 * @return the accountFax
	 */
	public String getAccountFax() {
		return accountFax;
	}
	/**
	 * @param accountFax the accountFax to set
	 */
	public void setAccountFax(String accountFax) {
		this.accountFax = accountFax;
	}
	/**
	 * @return the accountEmail
	 */
	public String getAccountEmail() {
		return accountEmail;
	}
	/**
	 * @param accountEmail the accountEmail to set
	 */
	public void setAccountEmail(String accountEmail) {
		this.accountEmail = accountEmail;
	}
	/**
	 * @return the locationName
	 */
	public String getLocationName() {
		return locationName;
	}
	/**
	 * @param locationName the locationName to set
	 */
	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}
	/**
	 * @return the locationID
	 */
	public String getLocationID() {
		return locationID;
	}
	/**
	 * @param locationID the locationID to set
	 */
	public void setLocationID(String locationID) {
		this.locationID = locationID;
	}
	/**
	 * @return the locationStreet
	 */
	public String getLocationStreet() {
		return locationStreet;
	}
	/**
	 * @param locationStreet the locationStreet to set
	 */
	public void setLocationStreet(String locationStreet) {
		this.locationStreet = locationStreet;
	}
	/**
	 * @return the locationLine2
	 */
	public String getLocationLine2() {
		return locationLine2;
	}
	/**
	 * @param locationLine2 the locationLine2 to set
	 */
	public void setLocationLine2(String locationLine2) {
		this.locationLine2 = locationLine2;
	}
	/**
	 * @return the locationCity
	 */
	public String getLocationCity() {
		return locationCity;
	}
	/**
	 * @param locationCity the locationCity to set
	 */
	public void setLocationCity(String locationCity) {
		this.locationCity = locationCity;
	}
	/**
	 * @return the locationState
	 */
	public String getLocationState() {
		return locationState;
	}
	/**
	 * @param locationState the locationState to set
	 */
	public void setLocationState(String locationState) {
		this.locationState = locationState;
	}
	/**
	 * @return the locationZip
	 */
	public String getLocationZip() {
		return locationZip;
	}
	/**
	 * @param locationZip the locationZip to set
	 */
	public void setLocationZip(String locationZip) {
		this.locationZip = locationZip;
	}
	/**
	 * @return the locationCountry
	 */
	public String getLocationCountry() {
		return locationCountry;
	}
	/**
	 * @param locationCountry the locationCountry to set
	 */
	public void setLocationCountry(String locationCountry) {
		this.locationCountry = locationCountry;
	}
	/**
	 * @return the locationPhone
	 */
	public String getLocationPhone() {
		return locationPhone;
	}
	/**
	 * @param locationPhone the locationPhone to set
	 */
	public void setLocationPhone(String locationPhone) {
		this.locationPhone = locationPhone;
	}
	/**
	 * @return the locationFax
	 */
	public String getLocationFax() {
		return locationFax;
	}
	/**
	 * @param locationFax the locationFax to set
	 */
	public void setLocationFax(String locationFax) {
		this.locationFax = locationFax;
	}
	/**
	 * @return the locationEmail
	 */
	public String getLocationEmail() {
		return locationEmail;
	}
	/**
	 * @param locationEmail the locationEmail to set
	 */
	public void setLocationEmail(String locationEmail) {
		this.locationEmail = locationEmail;
	}
	/**
	 * @return the locationContact
	 */
	public String getLocationContact() {
		return locationContact;
	}
	/**
	 * @param locationContact the locationContact to set
	 */
	public void setLocationContact(String locationContact) {
		this.locationContact = locationContact;
	}

	
	
	
	public boolean getSendMeds() {
		return sendMeds;
	}
	public void setSendMeds(boolean sendMeds) {
		this.sendMeds = sendMeds;
	}
	public boolean getSendPARs() {
		return sendPARs;
	}
	public void setSendPARs(boolean sendPARs) {
		this.sendPARs = sendPARs;
	}
	public boolean getSendProbs() {
		return sendProbs;
	}
	public void setSendProbs(boolean sendProbs) {
		this.sendProbs = sendProbs;
	}
	public boolean getNewcropManagedMeds() {
		return newcropManagedMeds;
	}
	public void setNewcropManagedMeds(boolean flg) {
		this.newcropManagedMeds = flg;
	}
	public boolean getNewcropManagedPARs() {
		return newcropManagedPARs;
	}
	public void setNewcropManagedPARs(boolean flg) {
		this.newcropManagedPARs = flg;
	}

	public boolean getPollStatus() {
		return pollStatus;
	}
	public void setPollStatus(boolean flg) {
		this.pollStatus = flg;
	}
	
	
	/**
	 * save() - Save this info to config file
	 * 
	 */
	public boolean save (){	
		return save( Pm.getERxDemo());
	}
	
	public boolean save( boolean demo ){
		
		
		XMLElement root;		// root element



		root = new XMLElement();
		root.setName( "ERxConfig" );

		
		
		
		// Set partner info
		{
			XMLElement c = new XMLElement();
			c.setName( "Partner" );
			{
			XMLElement p = new XMLElement();
			p.setName( "Name" );
			p.setContent( getPartnerName());
			c.addChild( p );
			}
			{
			XMLElement p = new XMLElement();
			p.setName( "Username" );
			p.setContent( getPartnerUsername());
			c.addChild( p );
			}
			{
			XMLElement p = new XMLElement();
			p.setName( "Password" );
			p.setContent( getPartnerPassword());
			c.addChild( p );
			}
			{
			XMLElement p = new XMLElement();
			p.setName( "ProductName" );
			p.setContent( getPartnerProduct() );
			c.addChild( p );
			}
			{
			XMLElement p = new XMLElement();
			p.setName( "ProductVersion" );
			p.setContent( getPartnerVersion());
			c.addChild( p );
			}
			
			root.addChild( c );
		}
		
		
		
		
		
		// Add account info
		{
			XMLElement c = new XMLElement();
			c.setName( "Account" );
			c.setAttribute( "ID", accountID );	// required
			
			{	// required
				XMLElement p = new XMLElement();
				p.setName( "AccountName" );
				p.setContent( getAccountName());
				c.addChild( p );
			}
			{	// required
				XMLElement p = new XMLElement();
				p.setName( "SiteID" );
				p.setContent( getSiteID());
				c.addChild( p );
			}
			{
				XMLElement p = new XMLElement();
				p.setName( "AccountAddress" );
				c.addChild( p );
				
				{
					XMLElement q = new XMLElement();
					q.setName( "address1" );
					q.setContent( getAccountStreet());
					p.addChild( q );
				}
				{
					XMLElement q = new XMLElement();
					q.setName( "address2" );
					q.setContent( getAccountLine2());
					p.addChild( q );
				}
				{
					XMLElement q = new XMLElement();
					q.setName( "city" );
					q.setContent( getAccountCity());
					p.addChild( q );
				}
				{
					XMLElement q = new XMLElement();
					q.setName( "state" );
					q.setContent( getAccountState());
					p.addChild( q );
				}
				{
					XMLElement q = new XMLElement();
					q.setName( "zip" );
					q.setContent( getAccountZip());
					p.addChild( q );
				}
				{
					XMLElement q = new XMLElement();
					q.setName( "country" );
					q.setContent( getAccountCountry());
					p.addChild( q );
				}
			}
			
			{
				XMLElement p = new XMLElement();
				p.setName( "Phone" );
				p.setContent( getAccountPhone());
				c.addChild( p );
			}
			{
				XMLElement p = new XMLElement();
				p.setName( "Fax" );
				p.setContent( getAccountFax());
				c.addChild( p );
			}
			{
				XMLElement p = new XMLElement();
				p.setName( "Email" );
				p.setContent( getAccountEmail());
				c.addChild( p );
			}
	
			root.addChild( c );
		}

		
		
		
		
		// Location info
		{
			XMLElement c = new XMLElement();
			c.setName( "Location" );
			if (( locationID != null ) && ( locationID.length() > 0 )) c.setAttribute( "ID", locationID );
			
			{	// required
				XMLElement p = new XMLElement();
				p.setName( "LocationName" );
				p.setContent( getLocationName());
				c.addChild( p );
			}
			{
				XMLElement p = new XMLElement();
				p.setName( "LocationAddress" );
				c.addChild( p );
				
				{
					XMLElement q = new XMLElement();
					q.setName( "address1" );
					q.setContent( getLocationStreet());
					p.addChild( q );
				}
				{
					XMLElement q = new XMLElement();
					q.setName( "address2" );
					q.setContent( getLocationLine2());
					p.addChild( q );
				}
				{
					XMLElement q = new XMLElement();
					q.setName( "city" );
					q.setContent( getLocationCity());
					p.addChild( q );
				}
				{
					XMLElement q = new XMLElement();
					q.setName( "state" );
					q.setContent( getLocationState());
					p.addChild( q );
				}
				{
					XMLElement q = new XMLElement();
					q.setName( "zip" );
					q.setContent( getLocationZip());
					p.addChild( q );
				}
				{
					XMLElement q = new XMLElement();
					q.setName( "country" );
					q.setContent( getLocationCountry());
					p.addChild( q );
				}
			}
			
			{
				XMLElement p = new XMLElement();
				p.setName( "Phone" );
				p.setContent( getLocationPhone());
				c.addChild( p );
			}
			{
				XMLElement p = new XMLElement();
				p.setName( "Fax" );
				p.setContent( getLocationFax());
				c.addChild( p );
			}
			{
				XMLElement p = new XMLElement();
				p.setName( "Email" );
				p.setContent( getLocationEmail());
				c.addChild( p );
			}
			{
				XMLElement p = new XMLElement();
				p.setName( "Contact" );
				p.setContent( getLocationContact());
				c.addChild( p );
			}
	
			root.addChild( c );
		}
		
		
		
		
		// Set Config info
		{
			XMLElement c = new XMLElement();
			c.setName( "Configs" );
			{
			XMLElement p = new XMLElement();
			p.setName( "ClickThruURL" );
			p.setContent( getClickThru());
			c.addChild( p );
			}
			{
			XMLElement p = new XMLElement();
			p.setName( "WebServicesURL" );
			p.setContent( getWebServices());
			c.addChild( p );
			}
			{
			XMLElement p = new XMLElement();
			p.setName( "NewCropManagedMeds" );
			p.setAttribute( "value", this.getNewcropManagedMeds() ? "true": "false" );
			c.addChild( p );
			}
			{
			XMLElement p = new XMLElement();
			p.setName( "NewCropManagedPARs" );
			p.setAttribute( "value", this.getNewcropManagedPARs() ? "true": "false" );
			c.addChild( p );
			}
			{
			XMLElement p = new XMLElement();
			p.setName( "SendMeds" );
			p.setAttribute( "value", this.getSendMeds() ? "true": "false" );
			c.addChild( p );
			}
			{
			XMLElement p = new XMLElement();
			p.setName( "SendPARs" );
			p.setAttribute( "value", this.getSendPARs() ? "true": "false" );
			c.addChild( p );
			}
			{
			XMLElement p = new XMLElement();
			p.setName( "SendProbs" );
			p.setAttribute( "value", this.getSendProbs() ? "true": "false" );
			c.addChild( p );
			}
			{
			XMLElement p = new XMLElement();
			p.setName( "PollStatus" );
			p.setAttribute( "value", this.getPollStatus() ? "true": "false" );
			c.addChild( p );
			}
			
			root.addChild( c );
		}
		
		
		// save to config file & return status
		boolean ret = root.toFile(  Pm.getOvdPath() + File.separator + ( demo ? fn_config_demo: fn_config ));
		
		setDemo( demo );
		Pm.setERxDemo();		// update the application flag
		
		return ret;
	}

	
	
	/**
	 * read() - read ERxConfig data from XML file
	 * 
	 */
	public boolean read(){ 
		return read( isDemo());
	}
	
	public boolean read( boolean demo ){
		
		
		XMLElement xml = new XMLElement();
		FileReader reader;
		
		try {
			reader = new FileReader( Pm.getOvdPath() + File.separator + (demo ? fn_config_demo: fn_config));  // demo ? fn_config_demo: fn_config 
		} catch (FileNotFoundException e) {
			System.out.println( "file not found:" + Pm.getOvdPath() + File.separator + ( demo ? fn_config_demo: fn_config ));
			return false;
		}
		
		try {
			xml.parseFromReader(reader);
		} catch (XMLParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		System.out.println( "root element=" + xml.getName());
		System.out.println( "num children=" + xml.countChildren());
		
		//XMLElement e = xml.getChildByName( "Account" ).getChildByName( "AccountAddress");

		//XMLElement e = xml.getElementByPathName( "Account/AccountAddress");

		
		//Enumeration children = e.enumerateChildren();
		
		//while ( children.hasMoreElements()){
		//	XMLElement e2 = (XMLElement) children.nextElement();
		//	System.out.println( e2.getName());
		//}

		
		// Read parsed info
		// Load Partner data into ERxConfig	
		
		XMLElement e;
		
		e = xml.getElementByPathName( "Partner/Name" );
		if ( e != null ) setPartnerName( e.getContent());

		//partnerID.setValue( eRxConfig.getPartnerID());
		
		e = xml.getElementByPathName( "Partner/Username" );
		if ( e != null ) setPartnerUsername( e.getContent());
		e = xml.getElementByPathName( "Partner/Password" );
		if ( e != null ) setPartnerPassword( e.getContent());
		e = xml.getElementByPathName( "Partner/ProductName" );
		if ( e != null ) setPartnerProduct( e.getContent());
		e = xml.getElementByPathName( "Partner/ProductVersion" );
		if ( e != null ) setPartnerVersion( e.getContent());
		
		
		// Load Account data into ERxConfig			
		e = xml.getElementByPathName( "Account" );
		if ( e != null ) setAccountID( (String) e.getAttribute( "ID", "" ));
		e = xml.getElementByPathName( "Account/AccountName" );
		if ( e != null ) setAccountName( e.getContent());
		e = xml.getElementByPathName( "Account/SiteID" );
		if ( e != null ) setSiteID( e.getContent());
		e = xml.getElementByPathName( "Account/AccountAddress/address1" );
		if ( e != null ) setAccountStreet( e.getContent());
		e = xml.getElementByPathName( "Account/AccountAddress/address2" );
		if ( e != null ) setAccountLine2( e.getContent());
		e = xml.getElementByPathName( "Account/AccountAddress/city" );
		if ( e != null ) setAccountCity( e.getContent());
		e = xml.getElementByPathName( "Account/AccountAddress/state" );
		if ( e != null ) setAccountState( e.getContent());
		e = xml.getElementByPathName( "Account/AccountAddress/zip" );
		if ( e != null ) setAccountZip( e.getContent());
		e = xml.getElementByPathName( "Account/AccountAddress/country" );
		if ( e != null ) setAccountCountry( e.getContent());
		e = xml.getElementByPathName( "Account/Phone" );
		if ( e != null ) setAccountPhone( e.getContent());
		e = xml.getElementByPathName( "Account/Fax" );
		if ( e != null ) setAccountFax( e.getContent());
		e = xml.getElementByPathName( "Account/Email" );
		if ( e != null ) setAccountEmail( e.getContent());

		
		// Load Location data into ERxConfig			
		e = xml.getElementByPathName( "Location" );
		if ( e != null ) setLocationID( (String) e.getAttribute( "ID", "" ));
		e = xml.getElementByPathName( "Location/LocationName" );
		if ( e != null ) setLocationName( e.getContent());
		//e = xml.getElementByPathName( "Location/siteID" );
		//if ( e != null ) setSiteID( e.getContent());
		e = xml.getElementByPathName( "Location/LocationAddress/address1" );
		if ( e != null ) setLocationStreet( e.getContent());
		e = xml.getElementByPathName( "Location/LocationAddress/address2" );
		if ( e != null ) setLocationLine2( e.getContent());
		e = xml.getElementByPathName( "Location/LocationAddress/city" );
		if ( e != null ) setLocationCity( e.getContent());
		e = xml.getElementByPathName( "Location/LocationAddress/state" );
		if ( e != null ) setLocationState( e.getContent());
		e = xml.getElementByPathName( "Location/LocationAddress/zip" );
		if ( e != null ) setLocationZip( e.getContent());
		e = xml.getElementByPathName( "Location/LocationAddress/country" );
		if ( e != null ) setLocationCountry( e.getContent());
		e = xml.getElementByPathName( "Location/Phone" );
		if ( e != null ) setLocationPhone( e.getContent());
		e = xml.getElementByPathName( "Location/Fax" );
		if ( e != null ) setLocationFax( e.getContent());
		e = xml.getElementByPathName( "Location/Email" );
		if ( e != null ) setLocationEmail( e.getContent());
		e = xml.getElementByPathName( "Location/Contact" );
		if ( e != null ) setLocationContact( e.getContent());

		
		
		// Load other config data
		e = xml.getElementByPathName( "Configs/ClickThruURL" );
		if ( e != null ) setClickThru( e.getContent());
		e = xml.getElementByPathName( "Configs/WebServicesURL" );
		if ( e != null ) setWebServices( e.getContent());

		
		// Checkboxes
		e = xml.getElementByPathName( "Configs/NewCropManagedMeds" );
		if ( e != null ) setNewcropManagedMeds( ((String) e.getAttribute( "value", "false" )).equals( "true" ));
		e = xml.getElementByPathName( "Configs/NewCropManagedPARs" );
		if ( e != null ) setNewcropManagedPARs( ((String) e.getAttribute( "value", "false" )).equals( "true" ));
		
		e = xml.getElementByPathName( "Configs/SendMeds" );
		if ( e != null ) setSendMeds( ((String) e.getAttribute( "value", "false" )).equals( "true" ));
		e = xml.getElementByPathName( "Configs/SendPARs" );
		if ( e != null ) setSendPARs( ((String) e.getAttribute( "value", "false" )).equals( "true" ));
		e = xml.getElementByPathName( "Configs/SendProbs" );
		if ( e != null ) setSendProbs( ((String) e.getAttribute( "value", "false" )).equals( "true" ));

		e = xml.getElementByPathName( "Configs/PollStatus" );
		if ( e != null ) setPollStatus( ((String) e.getAttribute( "value", "false" )).equals( "true" ));

		return true;
	}
	
	
	
	
	/**
	 * Demo config data vs. live config data
	 * 
	 */
	public static boolean isDemo(){
		
		if ( ! new File( Pm.getOvdPath() + File.separator + fn_config_demo ).exists()){
			System.out.println( "ERxConfig.isDemo(): fn_config_demo does not exist" );
			return false;
		}
		
		if ( ! new File( Pm.getOvdPath() + "/" + fn_config ).exists()){
			System.out.println( "ERxConfig.isDemo(): fn_config does not exist" );
			return true;
		}

		
		// both config files present, check erxdemo.xml		
		if ( ! new File( Pm.getOvdPath() + "/" + "erxdemo.xml" ).exists()){
			System.out.println( "ERxConfig.isDemo(): fn_erxdemo does not exist" );
			return false;
		}

		System.out.println( "ERxConfig.isDemo(): reading fn_erxdemo xml" );
		XMLElement xml = new XMLElement();
		FileReader reader;
		
		try {
			reader = new FileReader( Pm.getOvdPath() + "/" + "erxdemo.xml" );
		} catch (FileNotFoundException e) {
			System.out.println( "file not found:" + Pm.getOvdPath() + "/" + "erxdemo.xml" );
			return false;
		}
		
		try {
			xml.parseFromReader(reader);
		} catch (XMLParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		System.out.println( "root element=" + xml.getName());
		System.out.println( "num children=" + xml.countChildren());
		
		//XMLElement e = xml.getChildByName( "Account" ).getChildByName( "AccountAddress");

		XMLElement e;
		
		e = xml.getElementByPathName( "/ERxConfig/Demo" );
		if ( e == null ) return false;
		
		boolean ret = ( e.getContent().equalsIgnoreCase( "true" )) ? true: false;
		System.out.println( "ERxConfig.isDemo(): returning" + ret );
	
		return ret;
	}
	
	
	/**
	 * Demo config data vs. live config data
	 * 
	 */
	public static void setDemo( boolean demo ){

		System.out.println( "ERxConfig.setDemo(): setting demo=" + demo );
		
		// if not demo and demo config doesn't exist - we're done
		if ( ! demo && ! new File( Pm.getOvdPath() + File.separator + fn_config_demo ).exists() && ! new File( Pm.getOvdPath() + "/" + "erxdemo.xml" ).exists()){
			return;
		}

		XMLElement root;		// root element

		root = new XMLElement();
		root.setName( "ERxConfig" );

		
		// Set partner info
		XMLElement c = new XMLElement();
		c.setName( "Demo" );
		c.setContent( demo ? "true": "false" );
		root.addChild( c );

		// save to config file & return status
		root.toFile(  Pm.getOvdPath() + "/" + "erxdemo.xml" );
		return;
	}
}
			
		

