package org.wso2.appcloud.core.dao;

import com.google.common.base.Strings;
import org.wso2.appcloud.common.AppCloudException;
import org.wso2.appcloud.core.DBUtil;
import org.wso2.appcloud.core.SQLQueryConstants;
import org.wso2.appcloud.core.dto.CustomImage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CustomDockerImageDAO {
    private static final CustomDockerImageDAO CUSTOM_DOCKER_IMAGE_DAO = new CustomDockerImageDAO();

    /**
     * Constructor
     */
    private CustomDockerImageDAO() {

    }

    /**
     * Method for getting current instance.
     *
     * @return CustomImageDAO
     */
    public static CustomDockerImageDAO getInstance() {
        return CUSTOM_DOCKER_IMAGE_DAO;
    }


    /**
     * Add the record in AC_CUSTOM_DOCKER table for custom image
     *
     * @param dbConnection database connection
     * @param imageId      imageid (tenant domain-alphaneumeric url)
     * @param tenantId     tnant id
     * @param remoteUrl    remote url of image
     * @param updatedTime
     * @throws AppCloudException
     */
    public void addCustomDockerImage(Connection dbConnection, String imageId, int tenantId, String remoteUrl,
                                     String updatedTime)
            throws AppCloudException {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = dbConnection.prepareStatement(SQLQueryConstants.ADD_CUSTOM_DOCKER_IMAGE);
            preparedStatement.setString(1, imageId);
            preparedStatement.setInt(2, tenantId);
            preparedStatement.setString(3, remoteUrl);
            preparedStatement.setString(4, null);
            preparedStatement.setString(5, "scanning");
            preparedStatement.setString(6, updatedTime);
            preparedStatement.execute();
        } catch (SQLException e) {
            String msg = "Error while adding custom docker image, image id : " + imageId + " from remote url : " +
                         remoteUrl +
                         " for the tenant id : " + tenantId;
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closePreparedStatement(preparedStatement);
        }
    }

    public void updateCustomDockerImageRecord(Connection dbConnection, String imageId, String resultsJson,
                                              String status, String updatedTime)
            throws AppCloudException {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = dbConnection.prepareStatement(SQLQueryConstants.UPDATE_CUSTOM_DOCKER_IMAGE_DETAILS);
            preparedStatement.setString(1, resultsJson);
            preparedStatement.setString(2, status);
            preparedStatement.setString(3, updatedTime);
            preparedStatement.setString(4, imageId);
            preparedStatement.execute();
        } catch (SQLException e) {
            String msg = "Error while updating custom docker image, image id : " + imageId + " with test results : " +
                         resultsJson + " and status : " + status + " at : " + updatedTime;
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closePreparedStatement(preparedStatement);
        }
    }

    public boolean isImageAvailable(Connection dbConnection, String remoteUrl, int tenantId) throws AppCloudException {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = dbConnection.prepareStatement(SQLQueryConstants.IS_CUSTOM_IMAGE_AVAILABLE);
            preparedStatement.setString(1, remoteUrl);
            preparedStatement.setInt(2, tenantId);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            String msg = "Error while checking availability of custom docker image: " + remoteUrl +
                         " for tenant with tenant id: " + tenantId + ".";
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closeResultSet(resultSet);
            DBUtil.closePreparedStatement(preparedStatement);
        }
    }

    public List<CustomImage> getCustomImages(Connection dbConnection, int tenantId, String requiredStatus)
            throws AppCloudException {
        List<CustomImage> imagesList = new ArrayList<>();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            if (Strings.isNullOrEmpty(requiredStatus)) { // this means get all verified images
                preparedStatement = dbConnection.prepareStatement(SQLQueryConstants.GET_ALL_CUSTOM_IMAGES);
                preparedStatement.setInt(1, tenantId);
            } else {
                preparedStatement = dbConnection.prepareStatement(SQLQueryConstants.GET_CUSTOM_IMAGES_BY_STATUS);
                preparedStatement.setInt(1, tenantId);
                preparedStatement.setString(2, requiredStatus);
            }
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                CustomImage customImage = new CustomImage();
                customImage.setImageId(resultSet.getString(SQLQueryConstants.IMAGE_ID));
                customImage.setTenantId(resultSet.getInt(SQLQueryConstants.TENANT_ID));
                customImage.setRemoteUrl(resultSet.getString(SQLQueryConstants.REMOTE_URL));
                customImage.setResults(resultSet.getString(SQLQueryConstants.TEST_RESULTS_JSON));
                customImage.setStatus(resultSet.getString(SQLQueryConstants.STATUS));
                customImage.setLastUpdated(resultSet.getString(SQLQueryConstants.LAST_UPDATED));
                imagesList.add(customImage);
            }
        } catch (SQLException e) {
            String msg = "Error while retrieving custom docker images for  tenant with tenant id: " + tenantId + ".";
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closeResultSet(resultSet);
            DBUtil.closePreparedStatement(preparedStatement);
        }
        return imagesList;
    }

    public boolean deleteImage(Connection dbConnection, String imageId) throws AppCloudException {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = dbConnection.prepareStatement(SQLQueryConstants.DELETE_IMAGE);
            preparedStatement.setString(1, imageId);
            preparedStatement.executeUpdate();
            dbConnection.commit();
        } catch (SQLException e) {
            String msg = "Error while deleting custom docker image : " + imageId;
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closePreparedStatement(preparedStatement);
        }
        return true;
    }


    public CustomImage getImageById(Connection dbConnection, String imageId) throws AppCloudException {
        CustomImage customImage = new CustomImage();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = dbConnection.prepareStatement(SQLQueryConstants.GET_CUSTOM_IMAGE_BY_ID);
            preparedStatement.setString(1, imageId);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                customImage.setImageId(resultSet.getString(SQLQueryConstants.IMAGE_ID));
                customImage.setTenantId(resultSet.getInt(SQLQueryConstants.TENANT_ID));
                customImage.setRemoteUrl(resultSet.getString(SQLQueryConstants.REMOTE_URL));
                customImage.setResults(resultSet.getString(SQLQueryConstants.TEST_RESULTS_JSON));
                customImage.setStatus(resultSet.getString(SQLQueryConstants.STATUS));
                customImage.setLastUpdated(resultSet.getString(SQLQueryConstants.LAST_UPDATED));
            }
            return customImage;
        } catch (SQLException e) {
            String msg = "Error while retrieving custom docker image with  id: " + imageId + ".";
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closeResultSet(resultSet);
            DBUtil.closePreparedStatement(preparedStatement);
        }
    }

}
