// db 변수는 MongoDB에 접속된 현재 데이터베이스를 나타냅니다.

// DailyReport 컬렉션에 데이터 삽입
print("DailyReport 더미 데이터 생성 중...");
for (let i = 1; i <= 100; i++) {
    db.daily_report.insertOne({
        daily_report_no: `daily-${i}`,
        sleep_date: new Date(2025, 9, 21 - i), // 날짜를 역순으로 생성
        deep_sleep_time: Math.floor(Math.random() * 120) + 60,
        light_sleep_time: Math.floor(Math.random() * 180) + 120,
        rem_sleep_time: Math.floor(Math.random() * 90) + 30,
        deep_sleep_ratio: (0.2 + Math.random() * 0.2).toFixed(2),
        light_sleep_ratio: (0.4 + Math.random() * 0.2).toFixed(2),
        rem_sleep_ratio: (0.1 + Math.random() * 0.1).toFixed(2),
        memo: `오늘의 수면 기록 ${i}`,
        microwave_grades: Array.from({length: 3}, () => (Math.random() * 100).toFixed(2)),
        noise_event_types: ["코골이", "뒤척임", "잠꼬대"][Math.floor(Math.random() * 3)],
        analysis: [{
            title: "수면 분석",
            description: "수면 패턴이 불규칙합니다.",
            steps: ["규칙적인 취침 습관"],
            difficulty: "보통",
            effect: "수면의 질 개선"
        }],
        user_no: "756339877658854246"
    });
}
print("DailyReport 100개 생성 완료.");


// DailySleepRecord 컬렉션에 데이터 삽입
print("DailySleepRecord 더미 데이터 생성 중...");
for (let i = 1; i <= 100; i++) {
    db.daily_sleep_record.insertOne({
        daily_sleep_record_no: `record-${i}`,
        sleep_date: new Date(2025, 9, 21 - i),
        score: Math.floor(Math.random() * 40) + 60,
        bed_time: "23:30", // 예시
        wake_time: "07:00", // 예시
        total_sleep_time: Math.floor(Math.random() * 120) + 360,
        user_no: "756339877658854246"
    });
}
print("DailySleepRecord 100개 생성 완료.");


// PeriodicReport 컬렉션에 데이터 삽입
print("PeriodicReport 더미 데이터 생성 중...");
for (let i = 1; i <= 100; i++) {
    db.periodic_report.insertOne({
        periodic_report_no: `periodic-${i}`,
        type: i % 2 === 0 ? "weekly" : "monthly",
        date: new Date(2025, 9, 21 - i * 7),
        score: Math.floor(Math.random() * 40) + 60,
        total_sleep_time: Math.floor(Math.random() * 1000) + 3000,
        bed_time: `${Math.floor(Math.random() * 24)}:${Math.floor(Math.random() * 60)}`,
        deep_sleep_ratio: (0.2 + Math.random() * 0.2).toFixed(2),
        light_sleep_ratio: (0.4 + Math.random() * 0.2).toFixed(2),
        rem_sleep_ratio: (0.1 + Math.random() * 0.1).toFixed(2),
        improvement: `개선점 ${i}`,
        weakness: `약점 ${i}`,
        recommendation: `추천 ${i}`,
        score_prediction: {
            description: "예상 점수",
            scorePrediction: Array.from({length: 5}, () => Math.floor(Math.random() * 100))
        },
        user_no: "756339877658854246"
    });
}
print("PeriodicReport 100개 생성 완료.");