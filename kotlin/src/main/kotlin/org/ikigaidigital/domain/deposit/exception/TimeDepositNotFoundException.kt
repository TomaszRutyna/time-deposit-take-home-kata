package org.ikigaidigital.domain.deposit.exception

class TimeDepositNotFoundException(id: Int): RuntimeException("Time Deposit not found: $id")