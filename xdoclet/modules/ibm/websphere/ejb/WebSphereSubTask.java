/*
 * Copyright (c) 2001, 2002 The XDoclet team
 * All rights reserved.
 */
package xdoclet.modules.ibm.websphere.ejb;

import java.util.Collection;
import java.util.Iterator;

import xjavadoc.*;
import xjavadoc.ClassIterator;
import xjavadoc.XClass;
import xjavadoc.XCollections;
import xdoclet.XDocletException;

import xdoclet.modules.ejb.dd.AbstractEjbDeploymentDescriptorSubTask;

/**
 * This is a JavaDoc doclet plugin that can be used to generate EJB-related files from just one or a set of EJB bean
 * source files that uses custom EJBDoclet JavaDoc tags. In addition to the files generated by EJBDoclet,
 * WebSphereDoclet will also generate WebSphere specific deployment XML files.
 *
 * @author        Minh Yie, Denis Boutin
 * @created       15 August 2001
 * @ant.element   display-name="WebSphere" name="websphere" parent="xdoclet.modules.ejb.EjbDocletTask"
 * @version       $Revision: 1.1 $
 */
public class WebSphereSubTask extends AbstractEjbDeploymentDescriptorSubTask
{
	private final static String DD_PUBLICID_20 = "-//Sun Microsystems, Inc.//DTD Enterprise JavaBeans 2.0//EN";
	private final static String DD_SYSTEMID_20 = "http://java.sun.com/dtd/ejb-jar_2_0.dtd";

	private static String WEBSPHERE_EJB_JAR_XML_TEMPLATE_FILE = "resources/ejb-jar_xml.xdt";
    private static String WEBSPHERE_DEFAULT_BND_TEMPLATE_FILE = "resources/ibm-ejb-jar-bnd_xmi.xdt";
    private static String WEBSPHERE_DD_BND_FILE_NAME = "ibm-ejb-jar-bnd.xmi";
    private static String WEBSPHERE_DEFAULT_EXT_TEMPLATE_FILE = "resources/ibm-ejb-jar-ext_xmi.xdt";
    private static String WEBSPHERE_DD_EXT_FILE_NAME = "ibm-ejb-jar-ext.xmi";
    
    private static String WEBSPHERE_DD_EXT_PME_FILE_NAME = "ibm-ejb-jar-ext-pme.xmi";
    private static String WEBSPHERE_DEFAULT_EXT_PME_TEMPLATE_FILE = "resources/ibm-ejb-jar-ext-pme_xmi.xdt";
    
    private static String WEBSPHERE_DD_ACCESS_FILE_NAME = "ibm-ejb-access-bean.xmi";
    private static String WEBSPHERE_DEFAULT_ACCESS_BEAN_TEMPLATE_FILE = "resources/ibm-ejb-access-bean_xmi.xdt";

	private static String WEBSPHERE_DEFAULT_DBXMI_TEMPLATE_FILE = "resources/ibm_dbxmi.xdt";
	private static String WEBSPHERE_DEFAULT_SCHXMI_TEMPLATE_FILE = "resources/ibm_schxmi.xdt";
	private static String WEBSPHERE_DEFAULT_TBLXMI_TEMPLATE_FILE = "resources/ibm_tblxmi.xdt";        
	private static String WEBSPHERE_DEFAULT_RELATIONSHIP_TBLXMI_TEMPLATE_FILE = "resources/ibm_relationship_tblxmi.xdt";
	private static String WEBSPHERE_DEFAULT_MAPXMI_TEMPLATE_FILE = "resources/ibm_map_mapxmi.xdt";        
    private String _database = null;

    public WebSphereSubTask()
    {
        setUseIds(true);
    }

    /**
     * Called to validate configuration parameters.
     *
     * @exception XDocletException  Description of Exception
     */
    public void validateOptions() throws XDocletException
    {
        // WebSphere does not require a template url or a destination file
        //
        // super.validateOptions();
    }

    /**
     * Describe what the method does
     *
     * @exception XDocletException  Describe the exception
     */
    public void execute() throws XDocletException {
		setPublicId(DD_PUBLICID_20);
		setSystemId(DD_SYSTEMID_20);

		// will not work .... dumper.xdt does not exist
		/*
		setTemplateURL(getClass().getResource("resources/dumper.xdt"));
		setDestinationFile("dump");
		System.out.println("Generating dump");
		startProcess();
		*/


		setTemplateURL(getClass().getResource(WEBSPHERE_EJB_JAR_XML_TEMPLATE_FILE));
		setDestinationFile("ejb-jar.xml");
		System.out.println("Generating ejb-jar.xml");
		startProcess();

        setTemplateURL(getClass().getResource(WEBSPHERE_DEFAULT_BND_TEMPLATE_FILE));
        setDestinationFile(WEBSPHERE_DD_BND_FILE_NAME);
        System.out.println("Generating " + WEBSPHERE_DD_BND_FILE_NAME);
        startProcess();

        setTemplateURL(getClass().getResource(WEBSPHERE_DEFAULT_EXT_TEMPLATE_FILE));
        setDestinationFile(WEBSPHERE_DD_EXT_FILE_NAME);
        System.out.println("Generating " + WEBSPHERE_DD_EXT_FILE_NAME);
        startProcess();
        
        setTemplateURL(getClass().getResource(WEBSPHERE_DEFAULT_EXT_PME_TEMPLATE_FILE));
        setDestinationFile(WEBSPHERE_DD_EXT_PME_FILE_NAME);
        System.out.println("Generating " + WEBSPHERE_DD_EXT_PME_FILE_NAME);
        startProcess();

        /*
        setTemplateURL(getClass().getResource(WEBSPHERE_DEFAULT_ACCESS_BEAN_TEMPLATE_FILE));
        setDestinationFile(WEBSPHERE_DD_ACCESS_FILE_NAME);
        System.out.println("Generating " + WEBSPHERE_DD_ACCESS_FILE_NAME);
        startProcess();
        */
        
		setTemplateURL(getClass().getResource(WEBSPHERE_DEFAULT_MAPXMI_TEMPLATE_FILE));
		setDestinationFile("backends/" + getDb() + "/Map.mapxmi");
		System.out.println("Generating backends/" + getDb() + "/Map.mapxmi");
		startProcess();
        
        setTemplateURL(getClass().getResource(WEBSPHERE_DEFAULT_DBXMI_TEMPLATE_FILE));
        setDestinationFile("backends/" + getDb() + "/" + getSchema() + ".dbxmi");
        System.out.println("Generating backends/" + getDb() + "/" + getSchema() + ".dbxmi");
        startProcess();
        
		setTemplateURL(getClass().getResource(WEBSPHERE_DEFAULT_SCHXMI_TEMPLATE_FILE));
		setDestinationFile("backends/" + getDb() + "/" + getSchema()  + "_" + getUser() + "_sql.schxmi");
		System.out.println("Generating backends/" + getDb() + "/" + getSchema()  + "_" + getUser() + "_sql.schxmi");
		startProcess();
		
		Collection classes = getXJavaDoc().getSourceClasses();
		for (ClassIterator i = XCollections.classIterator(classes); i.hasNext();) {
			XClass clazz = i.next();
			//System.out.print(">> " + clazz.getName());
			// check tag ejb:persistence + sub tag table-name
			XTag tag = clazz.getDoc().getTag("ejb:persistence");
			if (tag != null) {
				String tableName = tag.getAttributeValue("table-name");
				//System.out.println("ejb:persistence table-name = '" + tableName + "'");
				String destinationFileName = "backends/" + getDb() + "/" + getSchema() + "_" + getUser() + "_sql_" + tableName + ".tblxmi";
				System.out.println("Generating " + destinationFileName);
				
				setTemplateURL(getClass().getResource(WEBSPHERE_DEFAULT_TBLXMI_TEMPLATE_FILE));
				setDestinationFile(destinationFileName);
				setHavingClassTag("ejb:persistence");
				setCurrentClass(clazz);
				startProcess();
			}
			// Now, check for relationships 
			for (Iterator methods = clazz.getMethods().iterator(); methods.hasNext();) {
				XMethod method = (XMethod)methods.next();
				if (method.getDoc().hasTag("websphere:relation")) {
					String tableName = method.getDoc().getTagAttributeValue("websphere:relation","table-name");
					setTemplateURL(getClass().getResource(WEBSPHERE_DEFAULT_RELATIONSHIP_TBLXMI_TEMPLATE_FILE));
					String destinationFileName = "backends/" + getDb() + "/" + getSchema() + "_" + getUser() + "_sql_" + tableName + ".tblxmi";
					setDestinationFile(destinationFileName);
					setCurrentClass(clazz);
					setCurrentMethod(method);
					System.out.println("\tGenerating M-M Relationship table: " + destinationFileName);
					startProcess();
					
				}
			}	

		}

/*
        if (atLeastOneCmpEntityBeanExists()) {
            setTemplateURL(getClass().getResource(WEBSPHERE_SCHEMA_TEMPLATE_FILE));
            setDestinationFile(WEBSPHERE_DD_SCHEMA_FILE_NAME);
            startProcess();
        }
*/
    }
    protected void generateForClass(XClass clazz) {
    	System.err.println("generateForClass(" + clazz.getName() + ")");
    }

    /**
     * Describe what the method does
     *
     * @exception XDocletException  Describe the exception
     */
    protected void engineStarted() throws XDocletException { }
    
    public void setDb(String dbName) { _database = dbName;
    }
    public String getDb() { return _database; }
    
    private String _schema = null;
	public void setSchema(String schema) { _schema = schema; }
	public String getSchema() { return _schema; }
	
	private String _user = null;
	public void setUser(String user) { _user = user; }
	public String getUser() { return _user; }
	
}