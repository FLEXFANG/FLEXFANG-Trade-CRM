-- FLEXFANG Trade CRM v0.1
-- Visible Core CRM menus for the Vue3 overlay.
-- Safe to re-run: all inserts are guarded by NOT EXISTS.

SET @crm_customer_parent_id := (
  SELECT `parent_id` FROM `system_menu`
  WHERE `permission` = 'crm:customer:query' AND `deleted` = b'0'
  LIMIT 1
);
SET @crm_root_id := COALESCE(@crm_customer_parent_id, 0);

-- type=1 DIRECTORY
INSERT INTO `system_menu`
(`name`,`permission`,`type`,`sort`,`parent_id`,`path`,`icon`,`component`,`component_name`,`status`,`visible`,`keep_alive`,`always_show`,`creator`,`create_time`,`updater`,`update_time`,`deleted`)
SELECT '外贸 CRM','',1,5,@crm_root_id,'trade','ep:ship','','',0,b'1',b'0',b'1','system',NOW(),'system',NOW(),b'0'
WHERE @crm_root_id <> 0
  AND NOT EXISTS (
    SELECT 1 FROM `system_menu`
    WHERE `parent_id`=@crm_root_id AND `path`='trade' AND `deleted`=b'0'
  );

SET @trade_root_id := (
  SELECT `id` FROM `system_menu`
  WHERE `parent_id`=@crm_root_id AND `path`='trade' AND `deleted`=b'0'
  ORDER BY `id` DESC
  LIMIT 1
);

-- type=2 MENU. Reuse crm:customer:query so the pages obey the existing CRM customer permission contract.
INSERT INTO `system_menu`
(`name`,`permission`,`type`,`sort`,`parent_id`,`path`,`icon`,`component`,`component_name`,`status`,`visible`,`keep_alive`,`always_show`,`creator`,`create_time`,`updater`,`update_time`,`deleted`)
SELECT '销售工作台','crm:customer:query',2,1,@trade_root_id,'workbench','ep:data-analysis','crm/trade/workbench/index','CrmTradeWorkbench',0,b'1',b'1',b'0','system',NOW(),'system',NOW(),b'0'
WHERE @trade_root_id IS NOT NULL
  AND NOT EXISTS (
    SELECT 1 FROM `system_menu`
    WHERE `parent_id`=@trade_root_id AND `path`='workbench' AND `deleted`=b'0'
  );

INSERT INTO `system_menu`
(`name`,`permission`,`type`,`sort`,`parent_id`,`path`,`icon`,`component`,`component_name`,`status`,`visible`,`keep_alive`,`always_show`,`creator`,`create_time`,`updater`,`update_time`,`deleted`)
SELECT '外贸客户','crm:customer:query',2,2,@trade_root_id,'customer','ep:user-filled','crm/trade/customer/index','CrmTradeCustomer',0,b'1',b'1',b'0','system',NOW(),'system',NOW(),b'0'
WHERE @trade_root_id IS NOT NULL
  AND NOT EXISTS (
    SELECT 1 FROM `system_menu`
    WHERE `parent_id`=@trade_root_id AND `path`='customer' AND `deleted`=b'0'
  );

-- Existing roles are not silently mutated. Super-admin sees the menus immediately;
-- normal roles can be granted the two new menu IDs through the standard role-menu UI.
