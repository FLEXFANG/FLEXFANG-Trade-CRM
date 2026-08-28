# FLEXFANG Trade CRM — RFQ v0.1

RFQ is a foreign-trade extension layer before quotation/contract. It reuses existing CRM Customer, Business and Product IDs instead of duplicating master data.

## Lifecycle

`NEW -> QUALIFYING -> QUOTING -> QUOTED -> WON / LOST / CANCELLED`

## Snapshot rule

RFQ items keep `productId` when a CRM product exists, but always store `productName`, specification, quantity and target price as a snapshot so historical inquiries are not changed by later product edits.

## API

- `POST /crm/trade-rfq/create`
- `PUT /crm/trade-rfq/update`
- `DELETE /crm/trade-rfq/delete?id=`
- `GET /crm/trade-rfq/get?id=`
- `GET /crm/trade-rfq/page`

## Validation

- Customer must exist.
- Optional Business must exist and belong to the same Customer.
- Optional Product IDs must resolve to enabled CRM products.
- RFQ number is unique per tenant.
- At least one line item is required.
