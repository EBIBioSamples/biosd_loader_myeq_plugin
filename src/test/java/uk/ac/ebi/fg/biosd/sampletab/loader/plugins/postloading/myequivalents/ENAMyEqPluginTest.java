package uk.ac.ebi.fg.biosd.sampletab.loader.plugins.postloading.myequivalents;

import org.junit.Test;

import uk.ac.ebi.fg.biosd.model.organizational.MSI;
import uk.ac.ebi.fg.biosd.sampletab.loader.Loader;
import uk.ac.ebi.fg.biosd.sampletab.loader.plugins.postloading.PostLoadingPlugInsProcessor;
import uk.ac.ebi.fg.myequivalents.managers.interfaces.EntityMappingManager;
import uk.ac.ebi.fg.myequivalents.managers.interfaces.EntityMappingSearchResult;
import uk.ac.ebi.fg.myequivalents.managers.interfaces.ServiceManager;
import uk.ac.ebi.fg.myequivalents.model.Entity;
import uk.ac.ebi.fg.myequivalents.model.Service;
import uk.ac.ebi.fg.myequivalents.resources.Resources;

import static org.junit.Assert.*;
import static java.lang.System.out;

public class ENAMyEqPluginTest
{
	@Test
	public void testENAMapping () throws Exception
	{
		ServiceManager srvMgr = Resources.getInstance ().getMyEqManagerFactory ().newServiceManager ();
		srvMgr.storeServices ( 
			new Service ( "ebi.ena.samples", "sample", "ENA Test Service", "A Test Service" ), 
			new Service ( MyEqSamplePlugIn.MYEQ_BIOSD_SERVICE_NAME, "sample", "BioSD Test Service", "A Test Service" ) 
		);
				
		Loader loader = new Loader ();
		MSI msi = loader.fromSampleData ( "target/test-classes/sampletab_examples/GEN-SRP003658.sampletab.csv" );
		PostLoadingPlugInsProcessor processor = new PostLoadingPlugInsProcessor ();
		processor.run ( msi );
		
		EntityMappingManager mapMgr = Resources.getInstance ().getMyEqManagerFactory ().newEntityMappingManager ();
		String maps = mapMgr.getMappingsAs ( "xml", true, 
			MyEqSamplePlugIn.MYEQ_BIOSD_SERVICE_NAME + ":SAMN00113752",
			MyEqSamplePlugIn.MYEQ_BIOSD_SERVICE_NAME + ":SAMN00114938"
		);
		
		out.println ( "Mapping result:\n" + maps );
		
		assertTrue ( "SRS115285 Not mapped!", maps.contains ( "SRS115285" ) && maps.contains ( "ebi.ena.samples" ) );
		assertTrue ( "SRS115285 Not mapped!", maps.contains ( "SRS116471" ) );
	}
}
