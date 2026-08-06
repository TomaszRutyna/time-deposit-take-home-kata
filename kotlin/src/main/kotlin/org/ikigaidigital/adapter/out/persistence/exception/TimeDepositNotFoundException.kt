package org.ikigaidigital.adapter.out.persistence.exception

class TimeDepositNotFoundException(id: Int): RuntimeException("Time Deposit not found: $id")