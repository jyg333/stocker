// src/config/chartOptions.ts

const chartOptions = {
    chart: {
        id: "portfolio-chart",
        zoom: { enabled: true },

    },
    xaxis: {
        type: "category",
        title: { text: "Trade Date" },
        labels: { style: { colors: "#ffffff" } },
    },
    yaxis: {
        title: { text: "Profit/Loss" },
        labels: { style: { colors: "#ffffff" } },
    },
    stroke: {
        curve: "smooth",
    },
    tooltip: {
        enabled: true,
        shared: true,
        theme: "light",
        y: {
            formatter: (value: number) => `${value.toLocaleString()} 원`,
        },
        x: {
            formatter: (value: string) => `날짜: ${value}`,
        },
    },
    colors: ["#4CAF50", "#F44336"],
    markers: {
        size: 5,
        colors: ["#ffffff"],
        strokeWidth: 2,
    },
    grid: {
        borderColor: "#ffffff",
    },
};

export default chartOptions;
