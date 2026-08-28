# FLEXFANG Trade CRM — Quotation v0.1

Quotation is a revisioned pre-PI commercial document. It remains separate from upstream CRM Contract because a sent quotation is not yet an order/contract.

## State machine

- `DRAFT -> SENT -> ACCEPTED`
- `SENT -> REJECTED`
- `DRAFT | SENT -> CANCELLED`
- `SENT | REJECTED -> SUPERSEDED` when a new revision is created

Only `DRAFT` can be edited or deleted. Once sent, changing price/terms requires `revise`, producing the next revision while preserving the old customer-facing version.

## Totals

`subtotal = sum(quantity * unitPrice)`

`totalAmount = subtotal - discountAmount + freight + insurance + otherCharge`

The service calculates these values; clients cannot submit authoritative totals.

## Relationships

- Customer: required
- Business: optional and must belong to Customer
- RFQ: optional and must belong to Customer
- Product: optional master-data link; product name/spec/price remain historical snapshots

PI/Contract conversion is intentionally deferred to the next slice so quotation acceptance and order creation remain separate lifecycle events.
