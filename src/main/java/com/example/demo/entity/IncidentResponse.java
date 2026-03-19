package com.example.demo.entity;

import jakarta.persistence.*; // @Id, @GeneratedValue 등을 위해 필요
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "incident_response")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class IncidentResponse extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 고유 번호

    @Column(unique = true)
    private Long parsedId; // 탐지된 데이터의 ID

    private Long adminId;  // 조치한 관리자 ID

    private String actionStatus; // OPEN, RESOLVED 등 상태

    private String actionNote;   // 조치 메모

    public static IncidentResponse createInitial(Long parsedId, Long adminId) {
        IncidentResponse res = new IncidentResponse();
        res.parsedId = parsedId;
        res.adminId = adminId;
        res.actionStatus = "OPEN";
        return res;
    }

    public void updateAction(String status, String note, Long adminId) {
        this.actionStatus = status;
        this.actionNote = note;
        this.adminId = adminId;
    }
}