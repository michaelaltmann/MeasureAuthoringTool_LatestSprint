/**
 * All the methods of this interface are to be called by a user with an 'Administrator' role.
 * The actual implementation of this interface on the server side should make sure to
 * check the session and verify that the current logged in user has an Administrator role.
 * If not, then we throw a InCorrectUserRoleException.
 */
package mat.client.admin.service;

import mat.client.admin.ManageOrganizationDetailModel;
import mat.client.admin.ManageOrganizationSearchModel;
import mat.client.admin.ManageUsersDetailModel;
import mat.client.admin.ManageUsersSearchModel;
import mat.shared.InCorrectUserRoleException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The Interface AdminService.
 */
@RemoteServiceRelativePath("admin")
public interface AdminService extends RemoteService {
	
	/**
	 * Delete user.
	 * 
	 * @param userId
	 *            the user id
	 * @throws InCorrectUserRoleException
	 *             the in correct user role exception
	 */
	public void deleteUser(String userId) throws InCorrectUserRoleException;
	
	/** Gets the organization.
	 * 
	 * @param key the key
	 * @return the organization */
	ManageOrganizationDetailModel getOrganization(String key);
	
	/**
	 * Gets the user.
	 * 
	 * @param key
	 *            the key
	 * @return the user
	 * @throws InCorrectUserRoleException
	 *             the in correct user role exception
	 */
	public ManageUsersDetailModel getUser(String key) throws InCorrectUserRoleException;
	
	/**
	 * Reset user password.
	 * 
	 * @param userid
	 *            the userid
	 * @throws InCorrectUserRoleException
	 *             the in correct user role exception
	 */
	public void resetUserPassword(String userid) throws InCorrectUserRoleException;
	
	/** Save update organization.
	 * 
	 * @param currentModel the current model
	 * @param updatedModel the updated model
	 * @return the save update organization result */
	SaveUpdateOrganizationResult saveUpdateOrganization(ManageOrganizationDetailModel currentModel,
			ManageOrganizationDetailModel updatedModel);
	/**
	 * Save update user.
	 * 
	 * @param model
	 *            the model
	 * @return the save update user result
	 * @throws InCorrectUserRoleException
	 *             the in correct user role exception
	 */
	public SaveUpdateUserResult saveUpdateUser(ManageUsersDetailModel model) throws InCorrectUserRoleException;
	
	/** Search organization.
	 * 
	 * @param key the key
	 * @param startIndex the start index
	 * @param pageSize the page size
	 * @return the manage organization search model */
	ManageOrganizationSearchModel searchOrganization(String key, int startIndex, int pageSize);
	/**
	 * Search users.
	 * 
	 * @param key
	 *            the key
	 * @param startIndex
	 *            the start index
	 * @param pageSize
	 *            the page size
	 * @return the manage users search model
	 * @throws InCorrectUserRoleException
	 *             the in correct user role exception
	 */
	public ManageUsersSearchModel searchUsers(String key, int startIndex, int pageSize) throws InCorrectUserRoleException;
	
	/** Gets the all organizations.
	 * 
	 * @return the all organizations */
	ManageOrganizationSearchModel getAllOrganizations();
}
