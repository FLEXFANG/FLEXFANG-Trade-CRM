-- FLEXFANG Trade CRM v0.1 - Quotation permission resources.
SET @crm_customer_menu_id := (SELECT `parent_id` FROM `system_menu` WHERE `permission`='crm:customer:query' AND `deleted`=b'0' LIMIT 1);
SET @crm_root_id := (SELECT `parent_id` FROM `system_menu` WHERE `id`=@crm_customer_menu_id AND `deleted`=b'0' LIMIT 1);
SET @crm_root_id := COALESCE(@crm_root_id,@crm_customer_menu_id,0);

INSERT INTO `system_menu` (`name`,`permission`,`type`,`sort`,`parent_id`,`path`,`icon`,`component`,`component_name`,`status`,`visible`,`keep_alive`,`always_show`,`creator`,`create_time`,`updater`,`update_time`,`deleted`)
SELECT '外贸报价查询','crm:trade-quotation:query',3,120,@crm_root_id,'','','','',0,b'1',b'0',b'0','system',NOW(),'system',NOW(),b'0' WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission`='crm:trade-quotation:query' AND `deleted`=b'0');
INSERT INTO `system_menu` (`name`,`permission`,`type`,`sort`,`parent_id`,`path`,`icon`,`component`,`component_name`,`status`,`visible`,`keep_alive`,`always_show`,`creator`,`create_time`,`updater`,`update_time`,`deleted`)
SELECT '外贸报价创建','crm:trade-quotation:create',3,121,@crm_root_id,'','','','',0,b'1',b'0',b'0','system',NOW(),'system',NOW(),b'0' WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission`='crm:trade-quotation:create' AND `deleted`=b'0');
INSERT INTO `system_menu` (`name`,`permission`,`type`,`sort`,`parent_id`,`path`,`icon`,`component`,`component_name`,`status`,`visible`,`keep_alive`,`always_show`,`creator`,`create_time`,`updater`,`update_time`,`deleted`)
SELECT '外贸报价修改','crm:trade-quotation:update',3,122,@crm_root_id,'','','','',0,b'1',b'0',b'0','system',NOW(),'system',NOW(),b'0' WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission`='crm:trade-quotation:update' AND `deleted`=b'0');
INSERT INTO `system_menu` (`name`,`permission`,`type`,`sort`,`parent_id`,`path`,`icon`,`component`,`component_name`,`status`,`visible`,`keep_alive`,`always_show`,`creator`,`create_time`,`updater`,`update_time`,`deleted`)
SELECT '外贸报价删除','crm:trade-quotation:delete',3,123,@crm_root_id,'','','','',0,b'1',b'0',b'0','system',NOW(),'system',NOW(),b'0' WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission`='crm:trade-quotation:delete' AND `deleted`=b'0');
INSERT INTO `system_menu` (`name`,`permission`,`type`,`sort`,`parent_id`,`path`,`icon`,`component`,`component_name`,`status`,`visible`,`keep_alive`,`always_show`,`creator`,`create_time`,`updater`,`update_time`,`deleted`)
SELECT '外贸报价状态','crm:trade-quotation:status',3,124,@crm_root_id,'','','','',0,b'1',b'0',b'0','system',NOW(),'system',NOW(),b'0' WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission`='crm:trade-quotation:status' AND `deleted`=b'0');
INSERT INTO `system_menu` (`name`,`permission`,`type`,`sort`,`parent_id`,`path`,`icon`,`component`,`component_name`,`status`,`visible`,`keep_alive`,`always_show`,`creator`,`create_time`,`updater`,`update_time`,`deleted`)
SELECT '外贸报价修订','crm:trade-quotation:revise',3,125,@crm_root_id,'','','','',0,b'1',b'0',b'0','system',NOW(),'system',NOW(),b'0' WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission`='crm:trade-quotation:revise' AND `deleted`=b'0');
