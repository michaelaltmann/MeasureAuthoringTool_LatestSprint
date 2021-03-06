package mat.server.service;

import java.util.List;

import mat.client.umls.service.VsacApiResult;
import mat.model.cql.CQLQualityDataSetDTO;


// TODO: Auto-generated Javadoc
/**
 * The Interface MeasureLibraryService.
 */
public interface VSACApiService {
	void inValidateVsacUser(String sessionId);
	
	boolean isAlreadySignedIn(String sessionId);
	
	VsacApiResult getAllVersionListByOID(String oid, String sessionId);
	
	VsacApiResult updateVSACValueSets(String measureId, String defaultExpId, String sessionId);
	
	boolean validateVsacUser(String userName, String password, String sessionId);
	
	VsacApiResult getMostRecentValueSetByOID(String oid, String expansionId, String sessionId);
	
	VsacApiResult getValueSetByOIDAndVersionOrExpansionId(String oid, String version, String expansionId, String sessionId);
	
	VsacApiResult updateAllVSACValueSetsAtPackage(String measureId, String sessionId);
	
	VsacApiResult getAllExpIdentifierList(String sessionId);

	VsacApiResult updateCQLVSACValueSets(List<CQLQualityDataSetDTO> appliedQDMList, String defaultExpId,
			String sessionId);
}
