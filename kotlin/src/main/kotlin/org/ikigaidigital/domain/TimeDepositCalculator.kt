package org.ikigaidigital.domain

import org.ikigaidigital.domain.model.TimeDeposit
import java.math.BigDecimal
import java.math.RoundingMode

class TimeDepositCalculator {
    fun updateBalance(xs: List<TimeDeposit>) {
        for (timeDespoit in xs) {
            var interest = 0.0
            if (timeDespoit.days > 30) {
                if (timeDespoit.planType == "student") {
                    if (timeDespoit.days < 366) {
                        interest += timeDespoit.balance * 0.03 / 12
                    }
                } else if (timeDespoit.planType == "premium") {
                    if (timeDespoit.days > 45) {
                        interest += timeDespoit.balance * 0.05 / 12
                    }
                } else if (timeDespoit.planType == "basic") {
                    interest += timeDespoit.balance * 0.01 / 12
                }
            }
            val a2d = BigDecimal(interest).setScale(2, RoundingMode.HALF_UP)
            timeDespoit.balance += a2d.toDouble()
        }
    }
}