

export function getWeekRange(year, week) {
  const date = new Date(year, 0, 1 + (week - 1) * 7);
  date.setDate(date.getDate() + (1 - date.getDay() + 7) % 7); // 定位到这周的周一

  const startDate = new Date(date);
  const endDate = new Date(date);
  endDate.setDate(date.getDate() + 6);

  return {
    startDate: formatDate(startDate),
    endDate: formatDate(endDate),
  };
}

export function getYearWeek(dateString) {
  const date = new Date(dateString);
  date.setHours(0, 0, 0, 0);
  // Thursday in current week decides the year. The week number
  // is the number of weeks between then and the first Thursday of the year.
  date.setDate(date.getDate() + 3 - (date.getDay() + 6) % 7);
  // January 4 is always in week 1.
  const week1 = new Date(date.getFullYear(), 0, 4);
  // Adjust to Thursday in week 1 and count number of weeks from date to week1.
  return 1 + Math.round(((date.getTime() - week1.getTime()) / 86400000 -
    3 + (week1.getDay() + 6) % 7) / 7);
}

export function formatDate(date) {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  return `${year}-${month}-${day}`;
}

export function getCurrentSemester() {
  const now = new Date();
  const year = now.getFullYear();
  const month = now.getMonth() + 1;

  // 假设上学期是 9月-次年2月，下学期是 3月-8月
  if (month >= 3 && month <= 8) {
    return `${year}-${year + 1}-2`; // 下学期
  } else {
    return `${year}-${year + 1}-1`; // 上学期 (或跨年到次年2月)
  }
}

export function getCurrentWeek() {
  const now = new Date();
  const onejan = new Date(now.getFullYear(), 0, 1);
  const week = Math.ceil(((now.getTime() - onejan.getTime()) / 86400000 + onejan.getDay() + 1) / 7);
  return week;
}
