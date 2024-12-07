const chartOptions = {
    chart: {
        id: "portfolio-chart",
        zoom: { enabled: true },
    },
    xaxis: { type: "category", title: { text: "Days" } },
    yaxis: { title: { text: "Price" } },
    tooltip: { shared: true, intersect: false },
    stroke: { curve: "smooth" },
};

export default chartOptions