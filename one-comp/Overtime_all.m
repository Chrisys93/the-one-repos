% Make a function out of this, that takes the officeIoT folder address and
% the number of runs as variables;

clear

OO1 = dlmread('officeIoT/ROR1', ' ', 0, 1);
HO1 = dlmread('homeIoT/ROR1', ' ', 0, 1);
BO1 = dlmread('buses/ROR1', ' ', 0, 1);
PO1 = dlmread('non-proc_proc/ROR1', ' ', 0, 1);


OO2 = dlmread('officeIoT/ROR2', ' ', 0, 1);
HO2 = dlmread('homeIoT/ROR2', ' ', 0, 1);
BO2 = dlmread('buses/ROR2', ' ', 0, 1);
PO2 = dlmread('non-proc_proc/ROR2', ' ', 0, 1);


OO3 = dlmread('officeIoT/ROR3', ' ', 0, 1);
HO3 = dlmread('homeIoT/ROR3', ' ', 0, 1);
BO3 = dlmread('buses/ROR3', ' ', 0, 1);
PO3 = dlmread('non-proc_proc/ROR3', ' ', 0, 1);


OO4 = dlmread('officeIoT/ROR4', ' ', 0, 1);
HO4 = dlmread('homeIoT/ROR4', ' ', 0, 1);
BO4 = dlmread('buses/ROR4', ' ', 0, 1);
PO4 = dlmread('non-proc_proc/ROR4', ' ', 0, 1);


OO5 = dlmread('officeIoT/ROR5', ' ', 0, 1);
HO5 = dlmread('homeIoT/ROR5', ' ', 0, 1);
BO5 = dlmread('buses/ROR5', ' ', 0, 1);
PO5 = dlmread('non-proc_proc/ROR5', ' ', 0, 1);


OO6 = dlmread('officeIoT/ROR6', ' ', 0, 1);
HO6 = dlmread('homeIoT/ROR6', ' ', 0, 1);
BO6 = dlmread('buses/ROR6', ' ', 0, 1);
PO6 = dlmread('non-proc_proc/ROR6', ' ', 0, 1);


[r3, c3] = size(OO1);

for repo = 1:c3
    oovertime(repo, 1) = mean(OO1(:, repo));
    if (isnan(mean(OO1(:, repo))))
        oovertime(repo, 1) = 0;
    end
    oovertime(repo, 2) = mean(OO2(:, repo));
    if (isnan(mean(OO2(:, repo))))
        oovertime(repo, 2) = 0;
    end
    oovertime(repo, 3) = mean(OO3(:, repo));
    if (isnan(mean(OO3(:, repo))))
        oovertime(repo, 3) = 0;
    end
    oovertime(repo, 4) = mean(OO4(:, repo));
    if (isnan(mean(OO4(:, repo))))
        oovertime(repo, 4) = 0;
    end
    oovertime(repo, 5) = mean(OO5(:, repo));
    if (isnan(mean(OO5(:, repo))))
        oovertime(repo, 5) = 0;
    end
    oovertime(repo, 6) =  mean(OO6(:, repo));
    if (isnan(mean(OO6(:, repo))))
        oovertime(repo, 6) = 0;
    end
    hovertime(repo, 1) = mean(HO1(:, repo));
    if (isnan(mean(HO1(:, repo))))
        hovertime(repo, 1) = 0;
    end
    hovertime(repo, 2) = mean(HO2(:, repo));
    if (isnan(mean(HO2(:, repo))))
        hovertime(repo, 2) = 0;
    end
    hovertime(repo, 3) = mean(HO3(:, repo));
    if (isnan(mean(HO3(:, repo))))
        hovertime(repo, 3) = 0;
    end
    hovertime(repo, 4) = mean(HO4(:, repo));
    if (isnan(mean(HO4(:, repo))))
        hovertime(repo, 4) = 0;
    end
    hovertime(repo, 5) = mean(HO5(:, repo));
    if (isnan(mean(HO5(:, repo))))
        hovertime(repo, 5) = 0;
    end
    hovertime(repo, 6) = mean(HO6(:, repo));
    if (isnan(mean(HO6(:, repo))))
        hovertime(repo, 6) = 0;
    end
    bovertime(repo, 1) = mean(BO1(:, repo));
    if (isnan(mean(BO1(:, repo))))
        bovertime(repo, 1) = 0;
    end
    bovertime(repo, 2) = mean(BO2(:, repo));
    if (isnan(mean(BO2(:, repo))))
        bovertime(repo, 2) = 0;
    end
    bovertime(repo, 3) = mean(BO3(:, repo));
    if (isnan(mean(BO3(:, repo))))
        bovertime(repo, 3) = 0;
    end
    bovertime(repo, 4) = mean(BO4(:, repo));
    if (isnan(mean(BO4(:, repo))))
        bovertime(repo, 4) = 0;
    end
    bovertime(repo, 5) = mean(BO5(:, repo));
    if (isnan(mean(BO5(:, repo))))
        bovertime(repo, 5) = 0;
    end
    bovertime(repo, 6) =  mean(BO6(:, repo));
    if (isnan(mean(BO6(:, repo))))
        bovertime(repo, 6) = 0;
    end
    povertime(repo, 1) = mean(PO1(:, repo));
    if (isnan(mean(PO1(:, repo))))
        povertime(repo, 1) = 0;
    end
    povertime(repo, 2) = mean(PO2(:, repo));
    if (isnan(mean(PO2(:, repo))))
        povertime(repo, 2) = 0;
    end
    povertime(repo, 3) = mean(PO3(:, repo));
    if (isnan(mean(PO3(:, repo))))
        povertime(repo, 3) = 0;
    end
    povertime(repo, 4) = mean(PO4(:, repo));
    if (isnan(mean(PO4(:, repo))))
        povertime(repo, 4) = 0;
    end
    povertime(repo, 5) = mean(PO5(:, repo));
    if (isnan(mean(PO5(:, repo))))
        povertime(repo, 5) = 0;
    end
    povertime(repo, 6) =  mean(PO6(:, repo));
    if (isnan(mean(PO6(:, repo))))
        povertime(repo, 6) = 0;
    end
end


figure

% subplot(2,1,1);
% yyaxis left
bar_handle = bar([oovertime(:,1), oovertime(:,2), oovertime(:,3), oovertime(:,4), oovertime(:,5), oovertime(:,6)]);
% title('Processing threads','fontsize',16)
xlabel('Repository number','fontsize',12) 
ylabel('Storage used (B)','fontsize',12)
% xlim([17 48]);
set(bar_handle(1),'FaceColor',[0,0.5,1])
set(bar_handle(2),'FaceColor',[0,1,0])




figure

% subplot(2,1,1);
% yyaxis left
bar([mean(nonzeros(oovertime(:,1))), mean(nonzeros(oovertime(:,2))), mean(nonzeros(oovertime(:,3))), mean(nonzeros(oovertime(:,4))), mean(nonzeros(oovertime(:,5))), mean(nonzeros(oovertime(:,6)));
    mean(nonzeros(hovertime(:,1))), mean(nonzeros(hovertime(:,2))), mean(nonzeros(hovertime(:,3))), mean(nonzeros(hovertime(:,4))), mean(nonzeros(hovertime(:,5))), mean(nonzeros(hovertime(:,6)));
    mean(nonzeros(bovertime(:,1))), mean(nonzeros(bovertime(:,2))), mean(nonzeros(bovertime(:,3))), mean(nonzeros(bovertime(:,4))), mean(nonzeros(bovertime(:,5))), mean(nonzeros(bovertime(:,6)));
    mean(nonzeros(povertime(:,1))), mean(nonzeros(povertime(:,2))), mean(nonzeros(povertime(:,3))), mean(nonzeros(povertime(:,4))), mean(nonzeros(povertime(:,5))), mean(nonzeros(povertime(:,6)))]);
% title('Processing threads','fontsize',16)
xlabel('Scenario Number','fontsize',12) 
ylabel('Storage used (B)','fontsize',12)
%TODO:
% Modify this:
lgd1 =legend('1: f; 1.1; 100; 3:1 \newline2: f; 3; 100; 4:1; \newline3: t; 4; 100; 4:1; \newline4: t; 2; 100; 1:2', ...
            '1: f; 1.3; 100; 3:1 \newline2: f; 3.2; 100; 4:1; \newline3: t; 2.2; 500; 4:1;  \newline4: t; 2; 500; 2:2', ...
            '1: f; 2; 100; 3:1 \newline2: f; 50; 100; 4:1; \newline3: f; 3; 100 4:1; \newline4: f; 2; 100; 4:2', ...
            '1: f; 1.6; 300; 3:1 \newline2: f; 3; 200; 4:1; \newline3: f; 4; 300 4:1; \newline4: f; 2; 300; 5:2', ...
            '1: f; 2.2; 600; 3:1 \newline2: f; 3.2; 300; 4:1; \newline3: f; 2.2; 600 4:1; \newline4: f; 2; 600; 3:1', ...
            '1: f; 2; 900; 4:1 \newline2: f; 50; 600; 4:1; \newline3: f; 3; 900 4:1; \newline4: f; 2; 900; 4:1', 'Location', 'southoutside');
title(lgd1, 'Scenario Number: Storage Mode; Processing Delay; proc Freshness Period; proc Shelf-Life; non-proc Shelf-Life; Compress:Delete Ratio; Non-Proc:Proc Ratio');
lgd1.NumColumns = 6;
% xlim([17 48]);
% set(bar_handle(1),'FaceColor',[0,0.5,1])
% set(bar_handle(2),'FaceColor',[0,1,0])


% procApp2.delay = 0.1
% procApp2.freshPer = [1; 1; 1; 1.5; 2; 3; 1]
% procApp2.procOhelf = [1.1; 1.3; 2; 1.6; 2.2; 3.3; 2]
% procApp2.nonprocOhelf = [100; 100; 100; 200; 300; 600]
% procApp2.destinationName = r
% procApp2.procOize = 50k
% # Otored message ratio to processing message (no. of messages to be stored vs. no. of messages to be processed)
% procApp2.compressRate = 1,4
% procApp2.passiveRate = [3,1; 3,1; 3,1; 3,1; 3,1; 4,1]


% procApp2.delay = [0.2; 0.2; 0.2; 0.1; 0.1; 0.1]
% procApp2.freshPer = [3; 3; 3; 2; 2; 2]
% procApp2.procOhelf = [3; 3.2; 50]
% procApp2.nonprocOhelf = [100; 100; 100; 200; 300; 600]
% procApp2.destinationName = r
% procApp2.procOize = 1M
% # Otored message ratio to processing message (no. of messages to be stored vs. no. of messages to be processed)
% procApp2.compressRate = [2,1; 2,1; 2,2; 2,2; 1,2; 1,2]
% procApp2.passiveRate = 4,1


% procApp.storageMode = [true; true; false; false; false; false]
% procApp.maxOtorTime = [3000; 3000]
% 
% 
% procApp3.interval = 0.7
% procApp3.delay = [0.3; 0.3; 0.3; 0.3; 0.1; 0.1]
% procApp3.freshPer = [3; 2; 2]
% procApp3.procOhelf = [4; 2.2; 3]
% procApp3.nonprocOhelf = [100; 500; 100; 300; 600; 900]
% procApp3.destinationName = r
% procApp3.procOize = 1M
% # Otored message ratio to processing message (no. of messages to be stored vs. no. of messages to be processed)
% procApp3.compressRate = 3,1
% procApp3.passiveRate = [4,1]
% procApp3.passive = false
% 
% 
% procApp2.delay = 0.1
% procApp2.freshPer = 1
% procApp2.procOhelf = 2
% procApp3.nonprocOhelf = [100; 500; 100; 300; 600; 900]
% procApp2.destinationName = r
% procApp2.procOize = 500k
% # Otored message ratio to processing message (no. of messages to be stored vs. no. of messages to be processed)
% procApp2.compressRate = 2,2
% procApp2.passiveRate = [4,1; 3,1; 5,2; 4,2; 2,2; 1,2]
% procApp2.passive = false


