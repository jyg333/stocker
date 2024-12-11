import {ApexOptions} from "apexcharts";

const chartOptions :ApexOptions= {
    chart: {
        id: "portfolio-chart",
        zoom: { enabled: true },
    },
    // xaxis: { type: "datetime", title: { text: "Days" } },
    xaxis: {
        type: "datetime",
        title: { text: "Date" },
        labels: {
            formatter: function (value) {
                return new Date(Number(value)).toLocaleDateString("en-US", {
                    year: "numeric",
                    month: "short",
                    day: "numeric",
                });
            },

        },
    },

    yaxis: { title: { text: "Price" } },
    tooltip: { x: {
            format: "yyyy-MM-dd", // 툴팁에 표시될 날짜 형식
        },shared: true, intersect: false },
    stroke: { curve: "smooth" },
};

export default chartOptions