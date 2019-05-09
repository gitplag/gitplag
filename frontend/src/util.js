import moment from "moment";

export function formatDate(dateString) {
  return moment(dateString).calendar();
}

export function times(n) {
  var accum = Array(Math.max(0, n));
  for (var i = 1; i < n; i++) accum[i] = i;
  return accum;
}