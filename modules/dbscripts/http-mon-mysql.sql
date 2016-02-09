-- 
-- Copyright 2015 WSO2 Inc. (http://wso2.org)
-- 
-- Licensed under the Apache License, Version 2.0 (the "License");
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
-- 
--     http://www.apache.org/licenses/LICENSE-2.0
-- 
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.
-- 

DROP DATABASE IF EXISTS mss_httpmon;

CREATE DATABASE IF NOT EXISTS mss_httpmon;

USE mss_httpmon;

CREATE TABLE IF NOT EXISTS REQUESTS_SUMMARY_PER_MINUTE
(
  webappName            VARCHAR(100) NOT NULL,
  webappType            VARCHAR(15),
  serverName            VARCHAR(45),
  averageRequestCount   BIGINT,
  averageResponseTime   BIGINT,
  httpSuccessCount      BIGINT,
  httpErrorCount        BIGINT,
  sessionCount          BIGINT,
  tenantId              INT,
  TIME VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS HTTP_STATUS
(
  webappName               VARCHAR(100) NOT NULL,
  serverName               VARCHAR(45),
  averageRequestCount      BIGINT,
  responseHttpStatusCode   INT,
  tenantId                 INT,
  TIME VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS LANGUAGE
(
  webappName            VARCHAR(100) NOT NULL,
  serverName            VARCHAR(45),
  averageRequestCount   BIGINT,
  tenantId              INT,
  LANGUAGE              VARCHAR(6),
  TIME VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS USER_AGENT_FAMILY
(
  webappName            VARCHAR(100) NOT NULL,
  serverName            VARCHAR(45),
  averageRequestCount   BIGINT,
  tenantId              INT,
  userAgentFamily       VARCHAR(15),
  TIME VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS OPERATING_SYSTEM
(
  webappName            VARCHAR(100) NOT NULL,
  serverName            VARCHAR(45),
  averageRequestCount   BIGINT,
  tenantId              INT,
  operatingSystem       VARCHAR(15),
  TIME VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS DEVICE_TYPE
(
  webappName            VARCHAR(100) NOT NULL,
  serverName            VARCHAR(45),
  averageRequestCount   BIGINT,
  tenantId              INT,
  deviceCategory        VARCHAR(100),
  TIME VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS REFERRER
(
  webappName            VARCHAR(100) NOT NULL,
  serverName            VARCHAR(45),
  averageRequestCount   BIGINT,
  tenantId              INT,
  referrer              VARCHAR(200),
  TIME VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS COUNTRY
(
  webappName            VARCHAR(100) NOT NULL,
  serverName            VARCHAR(45),
  averageRequestCount   BIGINT,
  tenantId              INT,
  country               VARCHAR(200),
  TIME VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS WEBAPP_CONTEXT
(
  webappName            VARCHAR(100) NOT NULL,
  serverName            VARCHAR(45),
  averageRequestCount   BIGINT,
  webappcontext         VARCHAR(200),
  tenantId              INT,
  TIME VARCHAR(100)
);

-- create temporary tables for DAS

CREATE TABLE IF NOT EXISTS TEMP_REQUESTS_SUMMARY_PER_MINUTE
(
  webappName            VARCHAR(100) NOT NULL,
  webappType            VARCHAR(15),
  serverName            VARCHAR(45),
  averageRequestCount   BIGINT,
  averageResponseTime   BIGINT,
  httpSuccessCount      BIGINT,
  httpErrorCount        BIGINT,
  sessionCount          BIGINT,
  tenantId              INT,
  TIME VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS TEMP_HTTP_STATUS
(
  webappName               VARCHAR(100) NOT NULL,
  serverName               VARCHAR(45),
  averageRequestCount      BIGINT,
  responseHttpStatusCode   INT,
  tenantId                 INT,
  TIME VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS TEMP_WEBAPP_CONTEXT
(
  webappName            VARCHAR(100) NOT NULL,
  serverName            VARCHAR(45),
  averageRequestCount   BIGINT,
  webappcontext         VARCHAR(200),
  tenantId              INT,
  TIME VARCHAR(100)
);


DELIMITER //

CREATE PROCEDURE copy_data()
BEGIN
  DELETE FROM mss_httpmon.REQUESTS_SUMMARY_PER_MINUTE;
  INSERT INTO mss_httpmon.REQUESTS_SUMMARY_PER_MINUTE SELECT * FROM mss_httpmon.TEMP_REQUESTS_SUMMARY_PER_MINUTE;
  DELETE FROM mss_httpmon.HTTP_STATUS;
  INSERT INTO mss_httpmon.HTTP_STATUS SELECT * FROM mss_httpmon.TEMP_HTTP_STATUS;
  DELETE FROM mss_httpmon.WEBAPP_CONTEXT;
  INSERT INTO mss_httpmon.WEBAPP_CONTEXT SELECT * FROM mss_httpmon.TEMP_WEBAPP_CONTEXT;
END //

DELIMITER ;


CREATE EVENT copy_event
    ON SCHEDULE
      EVERY 2 MINUTE
    COMMENT 'Copy data from temporary tables'
    DO
      CALL copy_data();

SET GLOBAL event_scheduler = ON;
