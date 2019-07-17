% Make a function out of this, that takes the reports_server folder address and
% the number of runs as variables;

clear

M1 = dlmread('reports_server/MDMR1', ' ', 0, 2);
S1 = dlmread('reports_server/RAMR1', ' ', 0, 2);
RP1 = dlmread('reports_server/RPrMR1', ' ', 0, 2);
RS1 = dlmread('reports_server/RSMR1', ' ', 0, 2);
PB1 = dlmread('reports_server/RPBWR1', ' ', 0, 2);
UB1 = dlmread('reports_server/RUPBWR1', ' ', 0, 2);
SB1 = dlmread('reports_server/RSBWR1', ' ', 0, 2);
CB1 = dlmread('reports_server/RCSBWR1', ' ', 0, 2);
RI1 = dlmread('reports_server/RISR1', ' ', 0, 2);
P1 = dlmread('reports_server/RPMR1', ' ', 0, 2);
ST1 = dlmread('reports_server/RSTR1', ' ', 0, 0);
PF1 = dlmread('reports_server/RPFR1', ' ', 0, 0);
PS1 = dlmread('reports_server/RSR1', ' ', 0, 0);
U1 = dlmread('reports_server/RUR1', ' ', 0, 0);
O1 = dlmread('reports_server/ROR1', ' ', 0, 0);


M2 = dlmread('reports_server/MDMR2', ' ', 0, 2);
S2 = dlmread('reports_server/RAMR2', ' ', 0, 2);
RP2 = dlmread('reports_server/RPrMR2', ' ', 0, 2);
RS2 = dlmread('reports_server/RSMR2', ' ', 0, 2);
UB2 = dlmread('reports_server/RUPBWR2', ' ', 0, 2);
PB2 = dlmread('reports_server/RPBWR2', ' ', 0, 2);
SB2 = dlmread('reports_server/RSBWR2', ' ', 0, 2);
CB2 = dlmread('reports_server/RCSBWR3', ' ', 0, 2);
RI2 = dlmread('reports_server/RISR2', ' ', 0, 2);
P2 = dlmread('reports_server/RPMR2', ' ', 0, 2);
ST2 = dlmread('reports_server/RSTR2', ' ', 0, 0);
PF2 = dlmread('reports_server/RPFR2', ' ', 0, 0);
PS2 = dlmread('reports_server/RSR2', ' ', 0, 0);
U2 = dlmread('reports_server/RUR2', ' ', 0, 0);
O2 = dlmread('reports_server/ROR2', ' ', 0, 0);


M3 = dlmread('reports_server/MDMR3', ' ', 0, 2);
S3 = dlmread('reports_server/RAMR3', ' ', 0, 2);
PB3 = dlmread('reports_server/RPBWR3', ' ', 0, 2);
UB3 = dlmread('reports_server/RUPBWR3', ' ', 0, 2);
SB3 = dlmread('reports_server/RSBWR3', ' ', 0, 2);
CB3 = dlmread('reports_server/RCSBWR3', ' ', 0, 2);
RI3 = dlmread('reports_server/RISR3', ' ', 0, 2);
RP3 = dlmread('reports_server/RPrMR3', ' ', 0, 2);
RS3 = dlmread('reports_server/RSMR3', ' ', 0, 2);
P3 = dlmread('reports_server/RPMR3', ' ', 0, 2);
ST3 = dlmread('reports_server/RSTR3', ' ', 0, 0);
PF3 = dlmread('reports_server/RPFR3', ' ', 0, 0);
PS3 = dlmread('reports_server/RSR3', ' ', 0, 0);
U3 = dlmread('reports_server/RUR3', ' ', 0, 0);
O3 = dlmread('reports_server/ROR3', ' ', 0, 0);


M4 = dlmread('reports_server/MDMR4', ' ', 0, 2);
S4 = dlmread('reports_server/RAMR4', ' ', 0, 2);
PB4 = dlmread('reports_server/RPBWR4', ' ', 0, 2);
UB4 = dlmread('reports_server/RUPBWR4', ' ', 0, 2);
SB4 = dlmread('reports_server/RSBWR4', ' ', 0, 2);
CB4 = dlmread('reports_server/RCSBWR4', ' ', 0, 2);
RI4 = dlmread('reports_server/RISR4', ' ', 0, 2);
RP4 = dlmread('reports_server/RPrMR4', ' ', 0, 2);
RS4 = dlmread('reports_server/RSMR4', ' ', 0, 2);
P4 = dlmread('reports_server/RPMR4', ' ', 0, 2);
ST4 = dlmread('reports_server/RSTR4', ' ', 0, 0);
PF4 = dlmread('reports_server/RPFR4', ' ', 0, 0);
PS4 = dlmread('reports_server/RSR4', ' ', 0, 0);
U4 = dlmread('reports_server/RUR4', ' ', 0, 0);
O4 = dlmread('reports_server/ROR4', ' ', 0, 0);


M5 = dlmread('reports_server/MDMR5', ' ', 0, 2);
S5 = dlmread('reports_server/RAMR5', ' ', 0, 2);
PB5 = dlmread('reports_server/RPBWR5', ' ', 0, 2);
UB5 = dlmread('reports_server/RUPBWR5', ' ', 0, 2);
SB5 = dlmread('reports_server/RSBWR5', ' ', 0, 2);
CB5 = dlmread('reports_server/RCSBWR5', ' ', 0, 2);
RI5 = dlmread('reports_server/RISR5', ' ', 0, 2);
RP5 = dlmread('reports_server/RPrMR5', ' ', 0, 2);
RS5 = dlmread('reports_server/RSMR5', ' ', 0, 2);
P5 = dlmread('reports_server/RPMR5', ' ', 0, 2);
ST5 = dlmread('reports_server/RSTR5', ' ', 0, 0);
PF5 = dlmread('reports_server/RPFR5', ' ', 0, 0);
PS5 = dlmread('reports_server/RSR5', ' ', 0, 0);
U5 = dlmread('reports_server/RUR5', ' ', 0, 0);
O5 = dlmread('reports_server/ROR5', ' ', 0, 0);


% M6 = dlmread('reports_server/MDMR6', ' ', 0, 2);
% S6 = dlmread('reports_server/RAMR6', ' ', 0, 2);
% UB6 = dlmread('reports_server/RUPBWR6', ' ', 0, 2);
% PB6 = dlmread('reports_server/RPBWR6', ' ', 0, 2);
% SB6 = dlmread('reports_server/RSBWR6', ' ', 0, 2);
% CB6 = dlmread('reports_server/RCSBWR6', ' ', 0, 2);
% RI6 = dlmread('reports_server/RISR6', ' ', 0, 2);
% RP6 = dlmread('reports_server/RPrMR6', ' ', 0, 2);
% RS6 = dlmread('reports_server/RSMR6', ' ', 0, 2);
% P6 = dlmread('reports_server/RPMR6', ' ', 0, 2);
% ST6 = dlmread('reports_server/RSTR6', ' ', 0, 2);
% PF6 = dlmread('reports_server/RPFR6', ' ', 0, 2);
% PS6 = dlmread('reports_server/RSR6', ' ', 0, 2);
% U6 = dlmread('reports_server/RUR6', ' ', 0, 2);
% O6 = dlmread('reports_server/ROR6', ' ', 0, 2);
[r2, c2] = size(M1);
[r3, c3] = size(S1);
maxstorM1 = zeros(c2, 0);
maxstor = zeros(c3, 0);

for inc = 5
    if inc == 1
        S = S1;
    end
    
    if inc == 2
        S = S2;
    end
    
    if inc == 3
        S = S3;
    end
    
    if inc == 4
        S = S4;
    end
    
    if inc == 5
        S = S5;
    end
  
%     if inc == 6
%         S = S6;
%     end
    
    s = 10000;
    col = 1;
    for i = 1:r3
        maxstor(i) = max(S(i, :));
        if maxstor(i) >= 15000 && i < s
            c0 = 1;
            c50 = 1;
            for c = 1:c3
                if S(i, c) >= 7500
                    fillperc50_100(c50) = S(i, c)/15000*100;
                    c50=c50+1;
                else
                    fillperc0_50(c0) = S(i, c)/15000*100;
                    c0=c0+1;
                end
            end
            figure
            bar(S(i, :));
            s = i; 
            %s = 1434 for 1000 cars
            %s = 1266 for 40 cars
        end

        c100 = 1;
        for c = 1:c3
            if S(i, c) >= 14000
                fill100(c100) = 1;
            else
                fill100(c100) = 0;
            end
            c100=c100+1;
        end
        filled100(i, :) = fill100;        
        fillperc100(col) = sum(fill100)/80*100;

        c50 = 1;
        for c = 1:c3
            if S(i, c) >= 7500
                fill50(c50) = 1;
            else
                fill50(c50) = 0;
            end
            c50=c50+1;
        end
        filled50(i, :) = fill50;
        fillperc50(col) = sum(fill50)/80*100;

        c25 = 1;
        for c = 1:c3
            if S(i, c) >= 4507
                fill25(c25) = 1;
            else
                fill25(c25) = 0;
            end
            c25=c25+1;
        end
        filled25(i, :) = fill25;
        fillperc25(col) = (sum(fill25))/80*100;
        col = col + 1;
    end

    maxval3 = max(maxstor);
    %TODO:
    % Make functions for these next time!
    if inc == 1
        maxstorS1 = maxstor;
        fillpercS1 = [fillperc25(:); fillperc50(:); fillperc100(:)];
        filled100S1 = filled100;
        filled50S1 = filled50;
        filled25S1 = filled25;
        for repo = 1:c3
            reposfillS1(repo, :) = mean(S(:, repo));
            reposfill(repo, inc) = reposfillS1(repo);           
        end
%         fillerrbarS1 = [sum(filled100(:)), sum(filled50(:)), sum(filled25(:))];
    end
    
    if inc == 2
        maxstorS2 = maxstor;
        fillpercS2 = [fillperc25(:); fillperc50(:); fillperc100(:)];
        for repo = 1:c3
            reposfillS2(repo) = mean(S(:, repo));
            reposfill(repo, inc) = reposfillS2(repo);           
        end
%        fillerrbarS3 = [sum(filled100(:)), sum(filled50(:)), sum(filled25(:))];
    end
    
    if inc == 3
        maxstorS3 = maxstor;
        fillpercS3 = [fillperc25(:); fillperc50(:); fillperc100(:)];
        for repo = 1:c3
            reposfillS3(repo) = mean(S(:, repo));
            reposfill(repo, inc) = reposfillS3(repo);           
        end
%         fillerrbarS3 = [sum(filled100(:)), sum(filled50(:)), sum(filled25(:))];
    end
    
    if inc == 4
        maxstorS4 = maxstor;
        fillpercS4 = [fillperc25(:); fillperc50(:); fillperc100(:)];
        for repo = 1:c3
            reposfillS4(repo) = mean(S(:, repo));
            reposfill(repo, inc) = reposfillS4(repo);           
        end
%         fillerrbarS4 = [sum(filled100(:)), sum(filled50(:)), sum(filled25(:))];
    end
    
    if inc == 5
        maxstorS5 = maxstor;
        fillpercS5 = [fillperc25(:); fillperc50(:); fillperc100(:)];
        for repo = 1:c3
            reposfillS5(repo) = mean(S(:, repo));
            reposfill(repo, inc) = reposfillS5(repo);           
        end
%         fillerrbarS3 = [sum(filled100(:)), sum(filled50(:)), sum(filled25(:))];
    end
    
%     if inc == 6
%         maxstorS6 = maxstor;
%         fillpercS6 = [fillperc25(:); fillperc50(:); fillperc100(:)];
%         for repo = 1:c3
%             reposfillS6(repo) = mean(S(:, repo));
%             reposfill(repo, inc) = reposfillS6(repo);           
%         end
% %         fillerrbarS4 = [sum(filled100(:)), sum(filled50(:)), sum(filled25(:))];
%     end
    
    fillerrbar(:, inc) = [sum(filled100(r3, :)), sum(filled50(r3, :)), sum(filled25(r3, :))];

%fillerrorbar = [fillerrbarS1(:); fillerrbarS2(:); fillerrbarS3(:); fillerrbarS4(:); fillerrbarS5(:)];
                %fillerrbarS6];
end



% figure
% hold on
% errorbar([25,50,100], fillerrbar(:,2), fillerrbar(:,2)-fillerrbar(:,1), fillerrbar(:,3)-fillpercerrbar(:,2), '--k', 'LineWidth', 2);
% errorbar([25,50,100], fillerrbarS2(:,2), fillerrbarS2(:,2)-fillerrbarS2(:,1), fillerrbarS2(:,3)-fillpercerrbarS2(:,2), '--b', 'LineWidth', 2);
% errorbar([25,50,100], fillerrbarS3(:,2), fillerrbarS3(:,2)-fillerrbarS3(:,1), fillerrbarS3(:,3)-fillpercerrbarS3(:,2), '--y', 'LineWidth', 2);
% errorbar([25,50,100], fillerrbarS4(:,2), fillerrbarS4(:,2)-fillerrbarS4(:,1), fillerrbarS4(:,3)-fillpercerrbarS4(:,2), '--m', 'LineWidth', 2);
% errorbar([25,50,100], fillerrbarS5(:,2), fillerrbarS5(:,2)-fillerrbarS5(:,1), fillerrbarS5(:,3)-fillpercerrbarS5(:,2), '--r', 'LineWidth', 2);
% errorbar([25,50,100], fillerrbarS6(:,2), fillerrbarS6(:,2)-fillerrbarS6(:,1), fillerrbarS6(:,3)-fillpercerrbarS6(:,2), '--g', 'LineWidth', 2);
% title('Percentage of repositories filled up according to storage depletion ratio');
% xlabel('Repo storage filled (%)');
% ylabel('Quantity of repos filled (%)');
% axis([0 125 0 100])
% legend('2 MB/ 3 s', '2 MB/2 s', '2 MB/s', '1 MB/3 s', '1 MB/2 s', '1 MB/s');
% hold off

% figure
% hold on
% bar(reposfill);
% title('Repositories storage used according to storage depletion ratio');
% xlabel('Repo number');
% ylabel('Repo storage used (MB)');
% legend('2 MB/ 3 s', '2 MB/2 s', '2 MB/s', '1 MB/3 s', '1 MB/2 s', '1 MB/s');
% hold off

for repo = 1:c3
    upspeeds1(repo, :) = [mean(CB1(:, repo)), mean(UB1(:, repo)), mean(PB1(:, repo))];
    upspeeds2(repo, :) = [mean(CB2(:, repo)), mean(UB2(:, repo)), mean(PB2(:, repo))];
    upspeeds3(repo, :) = [mean(CB3(:, repo)), mean(UB3(:, repo)), mean(PB3(:, repo))];
    upspeeds4(repo, :) = [mean(CB4(:, repo)), mean(UB4(:, repo)), mean(PB4(:, repo))];
    upspeeds5(repo, :) = [mean(CB5(:, repo)), mean(UB5(:, repo)), mean(PB5(:, repo))];
%     upspeeds6(repo, :) = [mean(CB6(:, repo)), mean(UB6(:, repo)), mean(PB6(:, repo))];
    inspeeds1(repo, :) = mean(RI1(:, repo));
    inspeeds2(repo, :) = mean(RI2(:, repo));
    inspeeds3(repo, :) = mean(RI3(:, repo));
    inspeeds4(repo, :) = mean(RI4(:, repo));
    inspeeds5(repo, :) = mean(RI5(:, repo));
%     inspeeds6(repo, :) = mean(RI6(:, repo));
    sat_perc1(repo, :) = PS1(1, repo)/sum(PS1(:, repo));
    sat_perc2(repo, :) = PS2(1, repo)/sum(PS2(:, repo));
    sat_perc3(repo, :) = PS3(1, repo)/sum(PS3(:, repo));
    sat_perc4(repo, :) = PS4(1, repo)/sum(PS4(:, repo));
    sat_perc5(repo, :) = PS5(1, repo)/sum(PS5(:, repo));
%     sat_perc6(repo, :) = mean(PS6(:, repo));
    fresh_perc1(repo, :) = PF1(1, repo)/sum(PF1(:, repo));
    fresh_perc2(repo, :) = PF2(1, repo)/sum(PF2(:, repo));
    fresh_perc3(repo, :) = PF3(1, repo)/sum(PF3(:, repo));
    fresh_perc4(repo, :) = PF4(1, repo)/sum(PF4(:, repo));
    fresh_perc5(repo, :) = PF5(1, repo)/sum(PF5(:, repo));
%     sat_perc6(repo, :) = mean(PF6(:, repo));
end

% For this, maybe take a few repositories, concentrate their storage
%evolution in lines and then show them in a graph, over time; as it's more
%interesting to see the evolution, use of storage and their stability.
%(check which repos should be monitored mainly by doing different stem
%plots for the different simulation scenarios)

%Repos:
%19, 20, 21, 41, 42, 43
%change mostly.
% figure
% stem3(S6, ':.');
% title('3D Stem plot of Repos Storage Usage','fontsize',16)
% xlabel('Repo Number','fontsize',12) 
% ylabel('Time(s)','fontsize',12)
% zlabel('Space used(B)','fontsize',12)

figure
plot(1:10800, S1(:,19), '-', 1:10800, S3(:,19), '-', 1:10800, S5(:,19), '-', 1:10800, S1(:,21), '--', 1:10800, S3(:,21), '--', 1:10800, S5(:,21), '--', 1:10800, S1(:,43), ':', 1:10800, S3(:,43), ':', 1:10800, S5(:,43), ':', 'LineWidth',1);
% title('3D Stem plot of Repos Storage Usage','fontsize',16)
ylabel('Number of stored messages','fontsize',12) 
xlabel('Time(s)','fontsize',12)
zlabel('Space used(B)','fontsize',12)
lgd = legend('R19 2 threads', 'R19 8 threads', 'R19 12 threads' , 'R19 16 threads', 'R21 2 threads', 'R21 8 threads', 'R21 12 threads' , 'R21 16 threads', 'R43 2 threads', 'R43 8 threads', 'R43 12 threads' , 'R43 16 threads');
lgd.FontSize = 9;
% lgd.NumColumns = 3;


% Maybe just show one side (irrespective of repo), and how much cache is
% used...either for all scenarios or the worst case - it simply shows that
% the cache is not used too much.
figure
stem3(P5, ':.');
% title('Stem plot of Repos Cache Usage','fontsize',16)
xlabel('Repo Number','fontsize',12) 
ylabel('Time(s)','fontsize',12)
zlabel('Number of cached messages','fontsize',12)

storage_30 = [S1(:, 30), S2(:, 30), S3(:, 30), S4(:, 30), S5(:, 30)];

% A potential for presenting, as mentioned above, a few different repos
%across time with, say, different lines or point identifiers...
% figure
% plot(storage_30);
% xlabel('Time (s)','fontsize',12) 
% ylabel('Total storage used (messages stored)','fontsize',12)
% lgd = legend('2 threads', '4 threads', '8 threads', '10 threads', '12 threads' , '16 threads');
% lgd.FontSize = 9;

% mdeleted = M1(10000, :)';
% figure
% bar(80:634, mdeleted);
% title('No. messages deleted from mobile nodes','fontsize',16)
% xlabel('Node address','fontsize',12) 
% ylabel('No. messages deleted','fontsize',12)

% proc_upspeeds_30 = [PB1(:, 30), PB2(:, 30), PB3(:, 30), PB4(:, 30), PB5(:, 30), PB6(:, 30)];
% uproc_upspeeds_30 = [UB1(:, 30), UB2(:, 30), UB3(:, 30), UB4(:, 30), UB5(:, 30), UB6(:, 30)];
% static_upspeeds_30 = [SB1(:, 30), SB2(:, 30), SB3(:, 30), SB4(:, 30), SB5(:, 30), SB6(:, 30)];
% figure
% subplot(3,1,1);
% plot(uproc_upspeeds_30);
% title('Upload speeds for cloud offloading','fontsize',16)
% xlabel('Time (s)','fontsize',12) 
% ylabel('Bandwidth used (B)','fontsize',12)
% lgd = legend('2 threads', '4 threads', '8 threads', '10 threads', '12 threads' , '16 threads');
% lgd.FontSize = 9;
% subplot(2,1,1);
% plot(proc_upspeeds_30);
% title('Upload speeds for processed messages','fontsize',16)
% xlabel('Time (s)','fontsize',12) 
% ylabel('Bandwidth used (B)','fontsize',12)
% lgd = legend('2 threads', '4 threads', '8 threads', '10 threads', '12 threads' , '16 threads');
% lgd.FontSize = 9;
% subplot(2,1,2);
% plot(static_upspeeds_30);
% title('Upload speeds for unprocessed messages','fontsize',16)
% xlabel('Time (s)','fontsize',12) 
% ylabel('Bandwidth used (B)','fontsize',12)
% lgd = legend('2 threads', '4 threads', '8 threads', '10 threads', '12 threads' , '16 threads');
% lgd.FontSize = 9;

% figure
% upspeeds1_30 = [SB4(:, 30) UB4(:, 30) PB4(:, 30)];
% subplot(1,2,1);
% bar(upspeeds1_30, 'stacked');
% lgd = legend('non-processing message upload', 'cloud offloading upload', 'processed message upload');
% lgd.FontSize = 9;
% xlabel('Time (s)','fontsize',12)  
% ylabel('Bandwidth used (B)','fontsize',12)
% inspeeds1_30 = RI4(:, 30);
% subplot(1,2,2);
% bar(inspeeds1_30);
% xlabel('Time (s)','fontsize',12)  
% ylabel('Bandwidth of input (B)','fontsize',12)


%This one is good for demonstrating the association between processing
%capacity and output bandwidth.
figure
upspeeds1_30 = [mean(SB1(:, 30)), mean(UB1(:, 30)), mean(PB1(:, 30));
                mean(SB2(:, 30)), mean(UB2(:, 30)), mean(PB2(:, 30)); 
                mean(SB3(:, 30)), mean(UB3(:, 30)), mean(PB3(:, 30)); 
                mean(SB4(:, 30)), mean(UB4(:, 30)), mean(PB4(:, 30));
                mean(SB5(:, 30)), mean(UB5(:, 30)), mean(PB5(:, 30))];
%                 mean(SB6(:, 30)), mean(UB6(:, 30)), mean(PB6(:, 30))];
            
storages_30 = [mean(RS1(:, 30)), mean(RP1(:, 30));
                mean(RS2(:, 30)), mean(RP2(:, 30)); 
                mean(RS3(:, 30)), mean(RP3(:, 30)); 
                mean(RS4(:, 30)), mean(RP4(:, 30)); 
                mean(RS5(:, 30)), mean(RP5(:, 30))]; 
%                 mean(RS6(:, 30)), mean(RP6(:, 30))];

% subplot(1,2,1);
yyaxis left
plot([2, 4, 8, 10, 12], upspeeds1_30, 'LineWidth', 1);
% set(bar_handle(1),'FaceColor',[0,0.5,1])
% set(bar_handle(2),'FaceColor',[0,1,0])
% set(bar_handle(3),'FaceColor',[0,1,0.5])
xlabel('No. of threads per repository','fontsize',12) 
ylabel('Bandwidth used (B)','fontsize',12)

yyaxis right
semilogy([2, 4, 8, 10, 12], storages_30, 'LineWidth', 1);
ylabel('Total storage used (B)','fontsize',12)
lgd = legend('non-processing message upload', 'cloud offloading upload', 'processed message upload', 'non-processing message storage', 'processing message storage');
lgd.FontSize = 9;

% inspeeds1_30 = [mean(RI1(:, 30)), mean(RI2(:, 30)), mean(RI3(:, 30)), mean(RI4(:, 30)), mean(RI5(:, 30)), mean(RI6(:, 30))];
% 
% subplot(1,2,2);
% bar([2, 4, 8, 10, 12, 16], inspeeds1_30);
% xlabel('No. of threads per repository','fontsize',12) 
% ylabel('Bandwidth of input (B)','fontsize',12)

% tstorages_30 = [RS4(:, 30) RP4(:, 30)];
% figure
% bar(tstorages_30, 'stacked');
% lgd = legend('non-processing message storage', 'processing message storage');
% lgd.FontSize = 9;
% xlabel('Time (s)','fontsize',12) 
% ylabel('Total storage used (B)','fontsize',12)


% This one is useful for showing how storage usage evolves with the
% increase of processing power.
% figure
% bar([2, 4, 8, 10, 12, 16], storages_30, 'stacked');
% lgd = legend('non-processing message storage', 'processing message storage');
% lgd.FontSize = 9;
% xlabel('No. of threads per repository','fontsize',12)
% ylabel('Total storage used (B)','fontsize',12)


% figure
% subplot(2,2,1);
% bar(upspeeds1, 'stacked');
% title('2 processing threads','fontsize',16)
% lgd =legend('non-processing message upload', 'cloud offloading upload', 'processed message upload');
% lgd.FontSize = 9;
% xlabel('Repository number','fontsize',12) 
% ylabel('Bandwidth used (B/s)','fontsize',12)
% subplot(2,2,1);
% bar(upspeeds2, 'stacked');
% title('4 processing threads')
% legend('non-processing message upload', 'cloud offloading upload', 'processed message upload');
% xlabel('Repository number') 
% ylabel('Bandwidth used (B/s)')
% subplot(3,2,3);
% bar(upspeeds3, 'stacked');
% title('8 processing threads')
% legend('non-processing message upload', 'cloud offloading upload', 'processed message upload');
% xlabel('Repository number') 
% ylabel('Bandwidth used (B/s)')
% subplot(2,2,2);
% bar(upspeeds4, 'stacked');
% title('16 processing threads','fontsize',16)
% lgd = legend('non-processing message upload', 'cloud offloading upload', 'processed message upload');
% lgd.FontSize = 9;
% xlabel('Repository number','fontsize',12) 
% ylabel('Bandwidth used (B/s)','fontsize',12)
% subplot(2,2,[3, 4]);
% inspeeds = [inspeeds1, inspeeds4];
% bar(inspeeds);
% title('Input Bandwidths','fontsize',16)
% lgd = legend('input speeds - 2 threads', 'input speeds - 16 threads');
% lgd.FontSize = 9;
% xlabel('Repository number','fontsize',12) 
% ylabel('Bandwidth used (B/s)','fontsize',12)

upspeeds1_store = upspeeds1(:, 1)';
upspeeds1_cloud = upspeeds1(:, 2)';
upspeeds1_proc = upspeeds1(:, 3)';
upspeeds5_store = upspeeds5(:, 1)';
upspeeds5_cloud = upspeeds5(:, 2)';
upspeeds5_proc = upspeeds5(:, 3)';
upspeeds1_total = upspeeds1(:, 1)' + upspeeds1(:, 2)' + upspeeds1(:, 3)';
upspeeds2_total = upspeeds2(:, 1)' + upspeeds2(:, 2)' + upspeeds2(:, 3)';
upspeeds3_total = upspeeds3(:, 1)' + upspeeds3(:, 2)' + upspeeds3(:, 3)';
upspeeds4_total = upspeeds4(:, 1)' + upspeeds4(:, 2)' + upspeeds4(:, 3)';
upspeeds5_total = upspeeds5(:, 1)' + upspeeds5(:, 2)' + upspeeds5(:, 3)';
% upspeeds6_total = upspeeds6(:, 1)' + upspeeds6(:, 2)' + upspeeds6(:, 3)';

% figure
% plot(1:80, upspeeds1_store, 1:80, upspeeds1_cloud, 1:80, upspeeds1_proc, 1:80, upspeeds6_store, 1:80, upspeeds6_cloud, 1:80, upspeeds6_proc);
% title('Processing threads','fontsize',16)
% lgd =legend('non-processing message upload for 2', 'cloud offloading upload for 2', 'processed message upload for 2', 'non-processing message upload for 16', 'cloud offloading upload for 16', 'processed message upload for 16');
% lgd.FontSize = 9;
% xlabel('Repository number','fontsize',12) 
% ylabel('Bandwidth used (B/s)','fontsize',12)


figure

% subplot(2,1,1);
yyaxis left
bar_handle = bar(upspeeds1(:,1:2), 'stacked');
% title('Processing threads','fontsize',16)
xlabel('Repository number','fontsize',12) 
ylabel('Bandwidth used (B/s)','fontsize',12)
ylim([0 5*10^6]);
xlim([17 48]);
set(bar_handle(1),'FaceColor',[0,0.5,1])
set(bar_handle(2),'FaceColor',[0,1,0])
% set(bar_handle(3),'FaceColor',[0,1,0.5])

yyaxis right
plot(1:80, upspeeds1_total, 'r-+', 1:80, upspeeds2_total, 'm-o', 1:80, upspeeds3_total, '-*', 1:80, upspeeds4_total, 'b-x', 1:80, upspeeds5_total, 'k->', 'LineWidth', 1);
lgd1 =legend('non-processing message upload for 2', 'cloud offloading upload for 2', 'message upload for 2', 'message upload for 4', 'message upload for 8', 'message upload for 10', 'message upload for 12', 'Location', 'southoutside');
lgd1.FontSize = 9;
% lgd1.NumColumns = 3;
ylabel('Bandwidth used (B/s)','fontsize',12)
ylim([0 5*10^6]);
xlim([17 48]);

% subplot(2,1,2);
% inspeeds = [inspeeds1, inspeeds4];
% bar(inspeeds);
% title('Input Bandwidths','fontsize',16)
% lgd = legend('2 threads', '16 threads');
% lgd.FontSize = 9;
% xlabel('Repository number','fontsize',12) 
% ylabel('Bandwidth used (B/s)','fontsize',12)


figure

% subplot(2,1,1);
% yyaxis left
bar_handle = bar([sat_perc1, sat_perc2, sat_perc4, sat_perc5]);
% title('Processing threads','fontsize',16)
xlabel('Repository number','fontsize',12) 
ylabel('Percentage (*100%) of storage messages satisfied','fontsize',12)
ylim([0 1]);
% xlim([17 48]);
set(bar_handle(1),'FaceColor',[0,0.5,1])
set(bar_handle(2),'FaceColor',[0,1,0])


figure

% subplot(2,1,1);
% yyaxis left
bar_handle = bar([fresh_perc1, fresh_perc2, fresh_perc4, fresh_perc5]);
% title('Processing threads','fontsize',16)
xlabel('Repository number','fontsize',12) 
ylabel('Percentage (*100%) of messages processed within freshness period','fontsize',12)
ylim([0 1]);
% xlim([17 48]);
set(bar_handle(1),'FaceColor',[0,0.5,1])
set(bar_handle(2),'FaceColor',[0,1,0])


figure

% subplot(2,1,1);
% yyaxis left
bar_handle = plot(1:80, O1, 1:80, O2, 1:80, O3, 1:80, O4, 1:80,  O5);
% title('Processing threads','fontsize',16)
xlabel('Repository number','fontsize',12)
ylabel('No. of Overtime Messages/repo','fontsize',12)
% ylim([0 5*10^6]);
% xlim([17 48]);


figure

% subplot(2,1,1);
% yyaxis left
bar_handle = plot(1:80, O1, 1:80, O2, 1:80, O3, 1:80, O4, 1:80,  O5);
% title('Processing threads','fontsize',16)
xlabel('Repository number','fontsize',12)
ylabel('No. of Overtime Messages/repo','fontsize',12)
% ylim([0 5*10^6]);
% xlim([17 48]);

