# FLEXFANG Trade CRM — Sample v0.1

Sample records can be created from an RFQ or directly from a Customer. They track the commercial promise (sample fee refundable on bulk order), payment, shipment/tracking, customer receipt and approval/rejection feedback.

## Lifecycle

`REQUESTED -> QUOTED -> PAID / PREPARING -> SENT -> RECEIVED -> APPROVED / REJECTED`

`CANCELLED` is available before completion.

## Relationships

- Customer: required
- Business: optional, must belong to Customer
- RFQ: optional, must belong to Customer
- Product: optional master-data link; name/spec/color/quantity/price remain snapshot fields

RFQs with Sample records cannot be deleted, preventing orphan sample history.
