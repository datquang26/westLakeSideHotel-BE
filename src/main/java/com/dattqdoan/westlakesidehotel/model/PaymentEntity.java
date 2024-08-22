package com.dattqdoan.westlakesidehotel.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "payment")
public class PaymentEntity {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne
        @JoinColumn(name = "service_booking_id", referencedColumnName = "id")
        private ServiceBookingEntity serviceBooking;

        @ManyToOne
        @JoinColumn(name = "booked_room_id", referencedColumnName = "bookingId")
        private BookedRoom bookedRoom;

        @Column(name = "status")
        private String status;

        @Column(name = "payment_date")
        private LocalDateTime paymentDate;

        @Column(name = "amount")
        private Float amount;

        @Column(name = "transaction_id")
        private String transactionId;

        @Column(name = "created_at")
        private LocalDateTime createdAt = LocalDateTime.now();

        @Column(name = "updated_at")
        private LocalDateTime updatedAt;

        @Column(name = "created_by", length = 40)
        private String createdBy;

        @Column(name = "updated_by", length = 40)
        private String updatedBy;

        @OneToMany(mappedBy = "payment")
        private List<TransactionLogEntity> transactionLogs;

        @PreUpdate
        protected void onUpdate() {
                updatedAt = LocalDateTime.now();
        }

}
