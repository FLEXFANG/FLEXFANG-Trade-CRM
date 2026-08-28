-- FLEXFANG Trade CRM v0.1
-- Permission resources for backend-only Trade Profile / RFQ / Sample APIs.
-- Visible route menus are intentionally deferred until the Vue pages exist.

SET @crm_customer_menu_id := (
  SELECT `parent_id` FROM `system_menu`
  WHERE `permission` = 'crm:customer:query' AND `deleted` = b'0'
  LIMIT 1
);
SET @crm_root_id := (
  SELECT `parent_id` FROM `system_menu`
  WHERE `id` = @crm_customer_menu_id AND `deleted` = b'0'
  LIMIT 1
);
SET @crm_root_id := COALESCE(@crm_root_id, @crm_customer_menu_id, 0);

-- type=3: BUTTON permission resource. status=0: enabled.
INSERT INTO `system_menu`
(`name`,`permission`,`type`,`sort`,`parent_id`,`path`,`icon`,`component`,`component_name`,`status`,`visible`,`keep_alive`,`always_show`,`creator`,`create_time`,`updater`,`update_time`,`deleted`)
SELECT '外贸档案查询','crm:trade-profile:query',3,90,@crm_root_id,'','','','',0,b'1',b'0',b'0','system',NOW(),'system',NOW(),b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission`='crm:trade-profile:query' AND `deleted`=b'0');

INSERT INTO `system_menu`
(`name`,`permission`,`type`,`sort`,`parent_id`,`path`,`icon`,`component`,`component_name`,`status`,`visible`,`keep_alive`,`always_show`,`creator`,`create_time`,`updater`,`update_time`,`deleted`)
SELECT '外贸档案维护','crm:trade-profile:update',3,91,@crm_root_id,'','','','',0,b'1',b'0',b'0','system',NOW(),'system',NOW(),b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission`='crm:trade-profile:update' AND `deleted`=b'0');

INSERT INTO `system_menu`
(`name`,`permission`,`type`,`sort`,`parent_id`,`path`,`icon`,`component`,`component_name`,`status`,`visible`,`keep_alive`,`always_show`,`creator`,`create_time`,`updater`,`update_time`,`deleted`)
SELECT '外贸询价查询','crm:trade-rfq:query',3,100,@crm_root_id,'','','','',0,b'1',b'0',b'0','system',NOW(),'system',NOW(),b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission`='crm:trade-rfq:query' AND `deleted`=b'0');

INSERT INTO `system_menu`
(`name`,`permission`,`type`,`sort`,`parent_id`,`path`,`icon`,`component`,`component_name`,`status`,`visible`,`keep_alive`,`always_show`,`creator`,`create_time`,`updater`,`update_time`,`deleted`)
SELECT '外贸询价创建','crm:trade-rfq:create',3,101,@crm_root_id,'','','','',0,b'1',b'0',b'0','system',NOW(),'system',NOW(),b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission`='crm:trade-rfq:create' AND `deleted`=b'0');

INSERT INTO `system_menu`
(`name`,`permission`,`type`,`sort`,`parent_id`,`path`,`icon`,`component`,`component_name`,`status`,`visible`,`keep_alive`,`always_show`,`creator`,`create_time`,`updater`,`update_time`,`deleted`)
SELECT '外贸询价修改','crm:trade-rfq:update',3,102,@crm_root_id,'','','','',0,b'1',b'0',b'0','system',NOW(),'system',NOW(),b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission`='crm:trade-rfq:update' AND `deleted`=b'0');

INSERT INTO `system_menu`
(`name`,`permission`,`type`,`sort`,`parent_id`,`path`,`icon`,`component`,`component_name`,`status`,`visible`,`keep_alive`,`always_show`,`creator`,`create_time`,`updater`,`update_time`,`deleted`)
SELECT '外贸询价删除','crm:trade-rfq:delete',3,103,@crm_root_id,'','','','',0,b'1',b'0',b'0','system',NOW(),'system',NOW(),b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission`='crm:trade-rfq:delete' AND `deleted`=b'0');

INSERT INTO `system_menu`
(`name`,`permission`,`type`,`sort`,`parent_id`,`path`,`icon`,`component`,`component_name`,`status`,`visible`,`keep_alive`,`always_show`,`creator`,`create_time`,`updater`,`update_time`,`deleted`)
SELECT '外贸样品查询','crm:trade-sample:query',3,110,@crm_root_id,'','','','',0,b'1',b'0',b'0','system',NOW(),'system',NOW(),b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission`='crm:trade-sample:query' AND `deleted`=b'0');

INSERT INTO `system_menu`
(`name`,`permission`,`type`,`sort`,`parent_id`,`path`,`icon`,`component`,`component_name`,`status`,`visible`,`keep_alive`,`always_show`,`creator`,`create_time`,`updater`,`update_time`,`deleted`)
SELECT '外贸样品创建','crm:trade-sample:create',3,111,@crm_root_id,'','','','',0,b'1',b'0',b'0','system',NOW(),'system',NOW(),b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission`='crm:trade-sample:create' AND `deleted`=b'0');

INSERT INTO `system_menu`
(`name`,`permission`,`type`,`sort`,`parent_id`,`path`,`icon`,`component`,`component_name`,`status`,`visible`,`keep_alive`,`always_show`,`creator`,`create_time`,`updater`,`update_time`,`deleted`)
SELECT '外贸样品修改','crm:trade-sample:update',3,112,@crm_root_id,'','','','',0,b'1',b'0',b'0','system',NOW(),'system',NOW(),b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission`='crm:trade-sample:update' AND `deleted`=b'0');

INSERT INTO `system_menu`
(`name`,`permission`,`type`,`sort`,`parent_id`,`path`,`icon`,`component`,`component_name`,`status`,`visible`,`keep_alive`,`always_show`,`creator`,`create_time`,`updater`,`update_time`,`deleted`)
SELECT '外贸样品删除','crm:trade-sample:delete',3,113,@crm_root_id,'','','','',0,b'1',b'0',b'0','system',NOW(),'system',NOW(),b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission`='crm:trade-sample:delete' AND `deleted`=b'0');

-- Role assignment is intentionally not hard-coded. After this migration, normal roles can be granted
-- these resources through the existing role-menu permission mechanism. Super-admin behavior is unchanged.
