# FLEXFANG Trade CRM v0.1 — Extension Architecture

## Baseline

- Upstream: `YunaiV/ruoyi-vue-pro`
- Base branch: `master-jdk17`
- Baseline SHA: `bff17062596b49f67c1c98f3fa3eb853aed093eb`
- Working branch: `feature/trade-crm-v0.1`

## Principle

Keep upstream CRM / ERP models intact wherever practical. Foreign-trade-specific data is added through an extension layer so upstream upgrades remain mergeable.

Phase 1 introduces `crm_trade_profile`, a 1:1 extension record identified by `(biz_type, biz_id)`.

Supported first-class bindings:

- `1` — CRM Clue / Lead
- `2` — CRM Customer / Company
- `4` — CRM Business / Opportunity

## Trade profile fields

### Market & buyer
- countryCode
- region
- city
- companyType
- sourceChannel
- website
- whatsapp
- linkedin
- importExperience
- annualPurchaseVolume

### Requirement & commercial
- targetProducts
- expectedMoq
- targetPrice
- currency
- certificationRequirement
- incoterm
- destinationPort

### Qualification & follow-up
- sampleStatus
- containerPotential
- fclProbability
- leadScore
- riskScore
- nextAction
- lostReason

## API

### Read

`GET /crm/trade-profile/get?bizType={type}&bizId={id}`

Permission: `crm:trade-profile:query`

### Upsert

`PUT /crm/trade-profile/save`

Permission: `crm:trade-profile:update`

The save endpoint performs an upsert by `(bizType, bizId)`.

## Code conventions

### companyType

- IMPORTER
- DISTRIBUTOR
- WHOLESALER
- RETAIL_CHAIN
- BRAND
- OEM_BUYER
- ONLINE_SELLER
- OTHER

### sourceChannel

- SHOPIFY
- META
- WHATSAPP
- EMAIL
- LINKEDIN
- FOUND
- EXHIBITION
- REFERRAL
- MANUAL

### incoterm

- EXW
- FOB
- CIF
- CFR
- DDP
- DAP

### sampleStatus

- NOT_REQUESTED
- REQUESTED
- QUOTED
- PAID
- SENT
- RECEIVED
- APPROVED
- REJECTED

### containerPotential

- SAMPLE
- LCL
- 20GP
- 40GP
- 40HQ
- UNKNOWN

## Next implementation slices

1. Add trade-profile permission/menu migration and role assignment path.
2. Copy trade profile automatically when a Clue is transformed into a Customer.
3. Add Trade RFQ and Sample entities.
4. Extend quotation/contract/order bridge rather than duplicating ERP sales-order logic.
5. Add shipment and trade-document entities.
6. Integrate the Vue3 front end after the backend APIs stabilize.
