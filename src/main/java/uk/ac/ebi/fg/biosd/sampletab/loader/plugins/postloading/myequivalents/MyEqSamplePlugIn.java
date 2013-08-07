package uk.ac.ebi.fg.biosd.sampletab.loader.plugins.postloading.myequivalents;

import java.util.HashSet;
import java.util.Set;

import uk.ac.ebi.fg.biosd.model.expgraph.BioSample;
import uk.ac.ebi.fg.biosd.model.organizational.BioSampleGroup;
import uk.ac.ebi.fg.biosd.model.organizational.MSI;
import uk.ac.ebi.fg.biosd.sampletab.loader.plugins.postloading.PostLoadingPlugIn;
import uk.ac.ebi.fg.myequivalents.managers.interfaces.EntityMappingManager;
import uk.ac.ebi.fg.myequivalents.resources.Resources;
import uk.ac.ebi.utils.regex.RegEx;

/**
 * 
 * TODO: Comment me!
 *
 * <dl><dt>date</dt><dd>5 Aug 2013</dd></dl>
 * @author Marco Brandizi
 *
 */
public abstract class MyEqSamplePlugIn implements PostLoadingPlugIn
{
	/** How is the service to link a BIOSD sample identified on myEq? */
	protected final static String MYEQ_BIOSD_SERVICE_NAME = "ebi.biosamples.samples";
	
	/** 
	 * Which BioSD pipeline does this plug-in support? This is a regular expression that must match the 
	 * {@link MSI#getAcc() submission accession} in {@link #run(MSI)}, to have the submission considered by this plug-in.
	 * 
	 */
	protected final RegEx bioSdPipeline;
	
	/** How the myEq service to which this plug-in maps is identified? */
	protected final String targetServiceName;
	
	/** 
	 * Tells the target accession to which the BioSD sample is mapped onto the myEQ service identified by {@link #getTargetServiceName()}.
	 * Returns null to tell that there is no mapping for this sample.  
	 */
	protected abstract String getTargetAccession ( BioSample smp );
	
	
	protected EntityMappingManager mappingMgr = Resources.getInstance ().getMyEqManagerFactory ().newEntityMappingManager ();

	
	protected MyEqSamplePlugIn ( String bioSdPipelineRE, String targetServiceName )
	{
		this.bioSdPipeline = new RegEx ( bioSdPipelineRE );
		this.targetServiceName = targetServiceName;
	}

	
	@Override
	public void run ( MSI msi )
	{
		if ( !bioSdPipeline.matches ( msi.getAcc () ) ) return;
		
		Set<BioSample> visited = new HashSet<BioSample> ();
		
		for ( BioSample smp: msi.getSamples () ) 
		{
			if ( smp.getId () != null || visited.contains ( smp ) ) continue;
			mapToMyEquivalents ( smp );
			visited.add ( smp );
		}
		
		for ( BioSampleGroup sg: msi.getSampleGroups () )
			for ( BioSample smp: sg.getSamples () )
			{
				if ( smp.getId () != null || visited.contains ( smp ) ) continue;
				mapToMyEquivalents ( smp );
				visited.add ( smp );
		}
	}

	protected void mapToMyEquivalents ( BioSample smp ) 
	{
		String targetAccId = getTargetAccession ( smp );
		if ( targetAccId == null ) return;
		
		mappingMgr.storeMappings ( MYEQ_BIOSD_SERVICE_NAME + ":" + smp.getAcc (), targetServiceName + ":" + targetAccId );
	}
}
