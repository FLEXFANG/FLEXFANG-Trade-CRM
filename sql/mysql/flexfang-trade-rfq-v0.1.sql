-- FLEXFANG Trade CRM v0.1
-- RFQ slice: inquiry header + product snapshots. Target MySQL 8.x.

CREATE TABLE IF NOT EXISTS `crm_trade_rfq` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `no` varchar(64) NOT NULL COMMENT 'RFQ编号',
  `customer_id` bigint NOT NULL COMMENT '客户编号',
  `business_id` bigint DEFAULT NULL COMMENT '商机编号',
  `owner_user_id` bigint NOT NULL COMMENT '负责人',
  `source_channel` varchar(32) DEFAULT NULL COMMENT '来源渠道',
  `received_time` datetime DEFAULT NULL COMMENT '收到询价时间',
  `due_time` datetime DEFAULT NULL COMMENT '客户要求回复时间',
  `status` varchar(32) NOT NULL DEFAULT 'NEW' COMMENT 'NEW/QUALIFYING/QUOTING/QUOTED/WON/LOST/CANCELLED',
  `currency` varchar(8) DEFAULT NULL COMMENT '币种',
  `incoterm` varchar(16) DEFAULT NULL COMMENT '贸易条款',
  `destination_port` varchar(128) DEFAULT NULL COMMENT '目的港',
  `certification_requirement` varchar(512) DEFAULT NULL COMMENT '认证要求',
  `remark` varchar(1000) DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_crm_trade_rfq_no` (`tenant_id`, `no`, `deleted`),
  KEY `idx_crm_trade_rfq_customer` (`tenant_id`, `customer_id`),
  KEY `idx_crm_trade_rfq_business` (`tenant_id`, `business_id`),
  KEY `idx_crm_trade_rfq_owner_status` (`tenant_id`, `owner_user_id`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='CRM 外贸 RFQ';

CREATE TABLE IF NOT EXISTS `crm_trade_rfq_item` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `rfq_id` bigint NOT NULL COMMENT 'RFQ编号',
  `product_id` bigint DEFAULT NULL COMMENT 'CRM产品编号',
  `product_name` varchar(255) NOT NULL COMMENT '产品名称快照',
  `specification` varchar(512) DEFAULT NULL COMMENT '规格/颜色/镜片等要求',
  `quantity` int NOT NULL COMMENT '询价数量',
  `target_price` decimal(18,4) DEFAULT NULL COMMENT '目标单价',
  `remark` varchar(512) DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`),
  KEY `idx_crm_trade_rfq_item_rfq` (`tenant_id`, `rfq_id`),
  KEY `idx_crm_trade_rfq_item_product` (`tenant_id`, `product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='CRM 外贸 RFQ 明细';
