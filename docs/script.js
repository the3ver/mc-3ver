// ==========================================================================
// MC 3ver — Modern Frontend Interactions
// ==========================================================================

document.addEventListener('DOMContentLoaded', () => {
  initTabs();
  initCopyButtons();
  initReleaseVersion();
  initNavbarScroll();
});

// OS Tab Switching
function initTabs() {
  const tabButtons = document.querySelectorAll('.tab-btn');
  const tabPanes = document.querySelectorAll('.tab-pane');

  tabButtons.forEach(button => {
    button.addEventListener('click', () => {
      const targetTab = button.dataset.tab;

      tabButtons.forEach(btn => btn.classList.remove('active'));
      tabPanes.forEach(pane => pane.classList.remove('active'));

      button.classList.add('active');
      const activePane = document.getElementById(`tab-${targetTab}`);
      if (activePane) {
        activePane.classList.add('active');
      }
    });
  });
}

// Copy to Clipboard
function initCopyButtons() {
  const copyButtons = document.querySelectorAll('.copy-btn');

  copyButtons.forEach(button => {
    button.addEventListener('click', async () => {
      const textToCopy = button.dataset.clipboard;
      if (!textToCopy) return;

      try {
        await navigator.clipboard.writeText(textToCopy);
        const originalText = button.textContent;
        button.textContent = 'Kopiert! ✓';
        button.style.color = '#10b981';
        button.style.borderColor = '#10b981';

        setTimeout(() => {
          button.textContent = originalText;
          button.style.color = '';
          button.style.borderColor = '';
        }, 2000);
      } catch (err) {
        console.error('Kopieren fehlgeschlagen:', err);
      }
    });
  });
}

// Fetch Latest Release Version from GitHub API
async function initReleaseVersion() {
  const versionTag = document.getElementById('releaseVersionTag');
  if (!versionTag) return;

  try {
    const response = await fetch('https://api.github.com/repos/the3ver/mc-3ver/releases/latest');
    if (response.ok) {
      const data = await response.json();
      if (data.tag_name) {
        versionTag.textContent = data.tag_name;
      }
    }
  } catch (e) {
    // Graceful fallback to default v1.0.0
    console.debug('Konnte Release-Version nicht von GitHub laden:', e);
  }
}

// Navbar Scroll Blur Effect
function initNavbarScroll() {
  const navbar = document.getElementById('navbar');
  if (!navbar) return;

  window.addEventListener('scroll', () => {
    if (window.scrollY > 20) {
      navbar.style.borderBottomColor = 'rgba(255, 255, 255, 0.12)';
      navbar.style.boxShadow = '0 10px 30px rgba(0, 0, 0, 0.6)';
    } else {
      navbar.style.borderBottomColor = 'rgba(255, 255, 255, 0.08)';
      navbar.style.boxShadow = 'none';
    }
  });
}
