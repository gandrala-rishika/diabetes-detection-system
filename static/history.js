document.addEventListener('DOMContentLoaded', function() {
    const bars = document.querySelectorAll('.prob-bar-fill');
    bars.forEach(bar => {
        const width = bar.style.width;
        bar.style.width = '0%';
        setTimeout(() => { bar.style.width = width; }, 200);
    });
});