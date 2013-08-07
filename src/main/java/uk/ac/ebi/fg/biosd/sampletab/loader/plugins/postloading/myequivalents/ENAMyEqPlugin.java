package uk.ac.ebi.fg.biosd.sampletab.loader.plugins.postloading.myequivalents;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.ebi.fg.biosd.model.expgraph.BioSample;
import uk.ac.ebi.fg.biosd.model.xref.DatabaseRefSource;

public class ENAMyEqPlugin extends MyEqSamplePlugIn
{
	private Logger log = LoggerFactory.getLogger ( this.getClass () );
	
	public ENAMyEqPlugin ()
	{
		super ( "^GEN-[^\\s]+$", "ebi.ena.samples" );
	}

	@Override
	protected String getTargetAccession ( BioSample smp )
	{
		String result = null;
		for ( DatabaseRefSource db: smp.getDatabases () )
			if ( "ENA SRA".equalsIgnoreCase ( StringUtils.trimToNull ( db.getName () ) ) )
				if ( result == null ) result = StringUtils.trimToNull ( db.getAcc () );
				else log.warn ( "Multiple 'ENA SRA' database references for the sample '" + smp.getAcc () + "', picking only one randomly" );
		return result;
	}
}
